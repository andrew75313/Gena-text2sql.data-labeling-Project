package org.example.datalabelingtool.domain.datasets.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.datalabelingtool.domain.datasets.dto.DatasetMetadataDto;
import org.example.datalabelingtool.domain.datasets.entity.DatasetColumn;
import org.example.datalabelingtool.domain.labels.dto.LabelResponseDto;
import org.example.datalabelingtool.domain.labels.entity.Label;
import org.example.datalabelingtool.domain.labels.repository.LabelRepository;
import org.example.datalabelingtool.domain.samples.dto.*;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.samples.entity.SampleStatus;
import org.example.datalabelingtool.domain.samples.repository.SampleRepository;
import org.example.datalabelingtool.domain.templates.entity.Template;
import org.example.datalabelingtool.domain.templates.repository.TemplateRepository;
import org.example.datalabelingtool.domain.users.dto.UserSimpleResponseDto;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.domain.users.repository.UserRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.example.datalabelingtool.global.exception.FileProcessingException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatasetService {

    private final SampleRepository sampleRepository;
    private final TemplateRepository templateRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    @Transactional
    public void uploadCsvFile(MultipartFile file, DatasetMetadataDto metadata) throws Exception {
        String datasetName = metadata.getDatasetName();
        String datasetDescription = metadata.getDatasetDescription();

        if (!sampleRepository.findByDatasetName(datasetName).isEmpty())
            throw new IllegalArgumentException("Dataset already exists");

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] columns = csvReader.readNext();

            if (columns == null) throw new FileProcessingException("CSV file is empty");

            Map<String, Integer> columnIndexMap = new HashMap<>();

            for (int i = 0; i < columns.length; i++) {
                for (DatasetColumn column : DatasetColumn.values()) {
                    if (columns[i].equalsIgnoreCase(column.toString())) {
                        columnIndexMap.put(column.toString(), i);
                    }
                }
            }

            if (columnIndexMap.size() < 4)
                throw new FileProcessingException("CSV file must contain 4 columns : sql_query, natural_question, no_template, sql_template");

            Integer noTemplateIndex = columnIndexMap.get(DatasetColumn.NO_SQL_TEMPLATE.toString());
            Integer sqlTemplateIndex = columnIndexMap.get(DatasetColumn.SQL_TEMPLATE.toString());

            String[] nextRecord;
            List<Sample> sampleList = new ArrayList<>();
            List<Template> templateList = new ArrayList<>();
            List<Long> templateNoList = new ArrayList<>();

            while ((nextRecord = csvReader.readNext()) != null) {
                JsonObject sampleData = new JsonObject();
                for (int i = 0; i < columns.length; i++) {
                    sampleData.addProperty(columns[i], nextRecord[i]);
                }
                Sample sample = Sample.builder()
                        .id(UUID.randomUUID().toString())
                        .sampleDataId(sampleData.get(DatasetColumn.SAMPLE_ID.toString()).getAsLong())
                        .naturalQuestion(sampleData.get(DatasetColumn.NATURAL_QUESTION.toString()).getAsString())
                        .sqlQuery(sampleData.get(DatasetColumn.SQL_QUERY.toString()).getAsString())
                        .datasetName(datasetName)
                        .datasetDescription(datasetDescription)
                        .versionId(1L)
                        .status(SampleStatus.CREATED)
                        .sampleData(sampleData.toString())
                        .labels(new ArrayList<>())
                        .build();

                sampleList.add(sample);

                Long templateNo = Long.parseLong(nextRecord[noTemplateIndex]);
                Template foundTemplate = templateRepository.findByTemplateNo(templateNo);

                if (foundTemplate == null && !templateNoList.contains(templateNo)) {
                    Template template = Template.builder()
                            .id(UUID.randomUUID().toString())
                            .templateNo(templateNo)
                            .content(nextRecord[sqlTemplateIndex])
                            .build();

                    templateList.add(template);
                    templateNoList.add(templateNo);
                }
            }

            sampleRepository.saveAll(sampleList);
            templateRepository.saveAll(templateList);

        } catch (Exception e) {
            throw new FileUploadException(e.getMessage(), e);
        }
    }

    @Transactional
    public InputStreamResource getCsvFile(String datasetName) {
        List<Sample> samples = sampleRepository.findLatestUpdatedSampleOfDataset(datasetName);

        if (samples.isEmpty()) throw new FileProcessingException("No data found for dataset");

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

            JsonObject firstSampleData = JsonParser.parseString(samples.get(0).getSampleData()).getAsJsonObject();
            String[] headers = firstSampleData.keySet().toArray(new String[0]);
            csvWriter.writeNext(headers);

            for (Sample sample : samples) {
                JsonObject sampleData = JsonParser.parseString(sample.getSampleData()).getAsJsonObject();
                sampleData.addProperty(DatasetColumn.NATURAL_QUESTION.toString(),
                        sample.getNaturalQuestion());
                sampleData.addProperty(DatasetColumn.SQL_QUERY.toString(), sample.getSqlQuery());

                String[] row = Arrays.stream(headers)
                        .map(header -> sampleData.has(header) ? sampleData.get(header).getAsString() : "")
                        .toArray(String[]::new);

                csvWriter.writeNext(row);
            }

            csvWriter.close();

            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        } catch (Exception e) {
            throw new FileProcessingException("Error generating CSV file");
        }
    }

    public SampleSameVerResponseDto getSampleById(String id) {
        Sample sample = findSample(id);

        List<SampleResponseDto> otherVersionsSamples = sampleRepository.getOtherSamplesOfSameVersion(sample.getVersionId(), sample.getSampleDataId())
                .stream()
                .map(this::toSampleResponseDto)
                .collect(toList());

        List<LabelResponseDto> labelResponseDtoList = new ArrayList<>();
        for (String labelId : sample.getLabels()) {
            Label foundLabel = labelRepository.findById(labelId).orElse(null);
            if (foundLabel == null || !foundLabel.getIsActive()) {
                continue;
            } else {
                LabelResponseDto labelResponseDto = LabelResponseDto.builder()
                        .labelId(foundLabel.getId())
                        .labelName(foundLabel.getName())
                        .build();
                labelResponseDtoList.add(labelResponseDto);
            }
        }

        return SampleSameVerResponseDto.builder()
                .id(sample.getId())
                .sampleId(sample.getSampleDataId())
                .naturalQuestion(sample.getNaturalQuestion())
                .sqlQuery(sample.getSqlQuery())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(sample.getStatus())
                .labels(labelResponseDtoList)
                .updatedBy(sample.getUpdatedBy())
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .otherVersionsSamples(otherVersionsSamples)
                .build();
    }

    public DataResponseDto getLatestUpdatesSamples() {
        List<SampleResponseDto> responseDtoList = sampleRepository.findLatestUpdatedSample().stream()
                .map(this::toSampleResponseDto)
                .collect(toList());

        return new DataResponseDto(responseDtoList);
    }

    @Transactional
    public DataResponseDto getRequestedSamples() {
        List<Sample> requestedSamples = sampleRepository.findRequestedSample();
        List<LatestSampleDto> responseDtoList = new ArrayList<>();

        if (requestedSamples.isEmpty()) {
            throw new FileProcessingException("No requested data found for dataset");
        }

        Map<Long, Map<String, List<Sample>>> groupedSamples = requestedSamples.stream()
                .collect(Collectors.groupingBy(
                        Sample::getSampleDataId,
                        Collectors.groupingBy(Sample::getUpdatedBy)
                ));

        for (Map.Entry<Long, Map<String, List<Sample>>> entry : groupedSamples.entrySet()) {
            Long sampleDataId = entry.getKey();
            Map<String, List<Sample>> userSamples = entry.getValue();

            Sample oldestSample = sampleRepository.findLatestValidSample(sampleDataId);
            if (oldestSample == null) {
                throw new FileProcessingException("No valid original sample found");
            }

            for (Map.Entry<String, List<Sample>> userEntry : userSamples.entrySet()) {
                String updatedBy = userEntry.getKey();
                List<Sample> samples = userEntry.getValue().stream()
                        .map(sample -> findSample(sample.getId()))
                        .toList();

                String latestSqlQuery;
                String latestNaturalQuestion;
                List<String> latestLabels;
                List<String> eventSampleIds = new ArrayList<>();

                Sample newSample = samples.stream()
                        .filter(sample -> sample.getStatus() == SampleStatus.REQUESTED_DELETE)
                        .findFirst()
                        .orElse(samples.get(0));
                SampleStatus newStatus;

                if (newSample.getStatus() == SampleStatus.REQUESTED_DELETE) {
                    latestSqlQuery = oldestSample.getSqlQuery();
                    latestNaturalQuestion = oldestSample.getNaturalQuestion();
                    latestLabels = oldestSample.getLabels();
                    eventSampleIds.add(newSample.getId());
                    newStatus = SampleStatus.REQUESTED_DELETE;
                } else {
                    Sample latestSampleForSql = samples.stream()
                            .filter(sample -> !sample.getSqlQuery().equals(oldestSample.getSqlQuery()))
                            .findFirst()
                            .orElse(oldestSample);

                    Sample latestSampleForNaturalQuestion = samples.stream()
                            .filter(sample -> !sample.getNaturalQuestion().equals(oldestSample.getNaturalQuestion()))
                            .findFirst()
                            .orElse(oldestSample);

                    Sample latestSampleForLabels = samples.stream()
                            .filter(sample -> !sample.getLabels().equals(oldestSample.getLabels()))
                            .findFirst()
                            .orElse(oldestSample);

                    latestSqlQuery = latestSampleForSql.getSqlQuery();
                    latestNaturalQuestion = latestSampleForNaturalQuestion.getNaturalQuestion();
                    latestLabels = latestSampleForLabels.getLabels();

                    if (!latestSqlQuery.equals(oldestSample.getSqlQuery())) {
                        eventSampleIds.add(latestSampleForSql.getId());
                    }
                    if (!latestNaturalQuestion.equals(oldestSample.getNaturalQuestion())) {
                        eventSampleIds.add(latestSampleForNaturalQuestion.getId());
                    }
                    if (!latestLabels.equals(oldestSample.getLabels())) {
                        eventSampleIds.add(latestSampleForLabels.getId());
                    }

                    newStatus = SampleStatus.REQUESTED_UPDATE;
                }


                UserSimpleResponseDto userSimpleResponseDto = null;


                User user = userRepository.findById(updatedBy).orElse(null);

                userSimpleResponseDto = UserSimpleResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build();

                List<LabelResponseDto> labelResponseDtoList = new ArrayList<>();
                for (String labelId : latestLabels) {
                    Label label = labelRepository.findById(labelId).orElse(null);
                    if (label == null || !label.getIsActive()) continue;
                    LabelResponseDto labelResponseDto = LabelResponseDto.builder()
                            .labelId(label.getId())
                            .labelName(label.getName())
                            .build();
                    labelResponseDtoList.add(labelResponseDto);
                }

                LatestSampleDto latestSampleDto = LatestSampleDto.builder()
                        .sampleId(oldestSample.getSampleDataId())
                        .naturalQuestion(latestNaturalQuestion)
                        .sqlQuery(latestSqlQuery)
                        .datasetName(oldestSample.getDatasetName())
                        .datasetDescription(oldestSample.getDatasetDescription())
                        .versionId(oldestSample.getVersionId())
                        .status(newStatus)
                        .labels(labelResponseDtoList)
                        .updatedBy(userSimpleResponseDto)
                        .latestEventIds(eventSampleIds)
                        .build();

                responseDtoList.add(latestSampleDto);
            }
        }

        return new DataResponseDto(responseDtoList);
    }

    @Transactional
    public DataResponseDto<List<SampleResponseDto>> updateSample(String id, SampleUpdateRequestDto requestDto) throws JsonProcessingException {
        String username = requestDto.getUsername();
        String sqlQuery = requestDto.getSqlQuery();
        String naturalQuestion = requestDto.getNaturalQuestion();
        List<String> labels = requestDto.getLabels();
        Boolean passed = requestDto.getPassed();
        Boolean deleted = requestDto.getDeleted();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        Sample sample = findSample(id);

        if (sample.getGroup().getId() == null) throw new EntityNotFoundException("Group of Sample not found");

        List<String> userIdList = findUsersAssignedToSample(sample.getId()).stream()
                .map(User::getId)
                .toList();

        if (!userIdList.contains(user.getId()))
            throw new IllegalArgumentException("User is not assigned to Sample");

        Long versionId = sample.getVersionId();

        Long latestSampleVersion = sampleRepository.findLatestVersionBySampleId(sample.getSampleDataId());

        if (latestSampleVersion > versionId) throw new IllegalArgumentException("Higher Version exists");

        if (passed && deleted)
            throw new IllegalArgumentException("Passed and Deleted can't be requested at the same time");

        if (passed || deleted) {
            if (!sqlQuery.isEmpty() || !naturalQuestion.isEmpty() || !labels.isEmpty())
                throw new IllegalArgumentException("SQL Query or Natural Question can't be requested when Passed or Deleted are requested");
        }

        if (!passed && !deleted && sqlQuery.isEmpty() && naturalQuestion.isEmpty() && labels.isEmpty())
            throw new IllegalArgumentException("No update requested");

        List<Sample> updatedSamples = new ArrayList<>();

        if (!sqlQuery.isEmpty()) {
            updatedSamples.add(createUpdatedSample(sample, user, SampleStatus.REQUESTED_UPDATE, sqlQuery, sample.getNaturalQuestion(), sample.getLabels()));
        }

        if (!naturalQuestion.isEmpty()) {
            updatedSamples.add(createUpdatedSample(sample, user, SampleStatus.REQUESTED_UPDATE, sample.getSqlQuery(), naturalQuestion, sample.getLabels()));
        }

        if (!labels.isEmpty()) {
            List<String> updatedLabels = labels.stream()
                    .filter(labelId -> labelRepository.findById(labelId).isPresent())
                    .toList();
            updatedSamples.add(createUpdatedSample(sample, user, SampleStatus.REQUESTED_UPDATE, sample.getSqlQuery(), sample.getNaturalQuestion(), updatedLabels));
        }

        if (passed) {
            updatedSamples.add(createUpdatedSample(sample, user, SampleStatus.REQUESTED_UPDATE, sample.getSqlQuery(), sample.getNaturalQuestion(), sample.getLabels()));
        }

        if (deleted) {
            updatedSamples.add(createUpdatedSample(sample, user, SampleStatus.REQUESTED_DELETE, sample.getSqlQuery(), sample.getNaturalQuestion(), sample.getLabels()));
        }

        sampleRepository.saveAll(updatedSamples);
        sample.getGroup().getSamples().addAll(updatedSamples);
        sampleRepository.flush();

        return new DataResponseDto<>(updatedSamples.stream().map(this::toSampleResponseDto).toList());
    }

    @Transactional
    public SampleApproveResponseDto approveSample(SampelApproveRequestDto requestDto) throws JsonProcessingException {
        List<String> requestedSampleIds = requestDto.getLatestEventIds();

        List<Sample> requestedSamples = sampleRepository.findAllById(requestedSampleIds);
        if (requestedSamples.isEmpty()) {
            throw new IllegalArgumentException("No requested samples found");
        }

        Sample sample = sampleRepository.findLatestValidSample(requestedSamples.get(0).getSampleDataId());
        if (sample == null) throw new EntityNotFoundException("Original Sample not found");

        String latestSqlQuery = sample.getSqlQuery();
        String latestNaturalQuestion = sample.getNaturalQuestion();
        List<String> latestLabels = new ArrayList<>(sample.getLabels());
        SampleStatus latestStatus = SampleStatus.UPDATED;

        for (Sample requestedSample : requestedSamples) {
            if (requestedSample.getStatus() == SampleStatus.REQUESTED_DELETE) {
                latestStatus = SampleStatus.DELETED;
                break;
            }
            if (!requestedSample.getSqlQuery().equals(sample.getSqlQuery())) {
                latestSqlQuery = requestedSample.getSqlQuery();
            }
            if (!requestedSample.getNaturalQuestion().equals(sample.getNaturalQuestion())) {
                latestNaturalQuestion = requestedSample.getNaturalQuestion();
            }
            if (!requestedSample.getLabels().equals(sample.getLabels())) {
                latestLabels = requestedSample.getLabels();
            }
        }

        String newSampleId = UUID.randomUUID().toString();
        if (!latestSqlQuery.equals(sample.getSqlQuery()) ||
                !latestNaturalQuestion.equals(sample.getNaturalQuestion()) ||
                !latestLabels.equals(sample.getLabels()) ||
                latestStatus != SampleStatus.UPDATED) {

            Sample newSample = Sample.builder()
                    .id(newSampleId)
                    .sampleDataId(sample.getSampleDataId())
                    .naturalQuestion(latestNaturalQuestion)
                    .sqlQuery(latestSqlQuery)
                    .labels(latestLabels)
                    .datasetName(sample.getDatasetName())
                    .datasetDescription(sample.getDatasetDescription())
                    .versionId(sample.getVersionId() + 1)
                    .status(latestStatus)
                    .sampleData(sample.getSampleData())
                    .updatedBy(requestedSamples.get(0).getUpdatedBy())
                    .group(sample.getGroup())
                    .build();

            sampleRepository.save(newSample);

            newSample.getGroup().getSamples().add(newSample);
        }

        List<Sample> allRequestedSamples = sampleRepository.findRequestedBySampleIdAndVersionId(sample.getSampleDataId() ,
                sample.getVersionId());

        for (Sample requestedSample : allRequestedSamples) {
            if (requestedSampleIds.contains(requestedSample.getId())) {
                requestedSample.updateStatus(SampleStatus.APPROVED);
            } else {
                requestedSample.updateStatus(SampleStatus.REJECTED);
            }
            requestedSample.updateVersionId(requestedSample.getVersionId() + 1);
        }

        return SampleApproveResponseDto.builder()
                .sampleId(newSampleId)
                .status(SampleStatus.UPDATED.toString())
                .approvedAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public SampleRejectResponseDto rejectSample(@Valid String id) {
        Sample sample = findSample(id);
        sample.updateStatus(SampleStatus.REJECTED);
        return SampleRejectResponseDto.builder()
                .sampleId(sample.getId())
                .status(SampleStatus.REJECTED.toString())
                .rejectedAt(LocalDateTime.now())
                .build();
    }

    private List<User> findUsersAssignedToSample(String id) {
        return sampleRepository.findUsersAssignedToSample(id);
    }

    private SampleResponseDto toSampleResponseDto(Sample sample) {
        UserSimpleResponseDto userSimpleResponseDto = null;

        sample = sampleRepository.findById(sample.getId()).orElse(null);

        if (sample.getUpdatedBy() != null) {
            User user = userRepository.findById(sample.getUpdatedBy()).orElse(null);

            userSimpleResponseDto = UserSimpleResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();
        }

        List<LabelResponseDto> labelResponseDtoList = new ArrayList<>();
        for (String labelId : sample.getLabels()) {
            Label label = labelRepository.findById(labelId).orElse(null);
            if (label == null || !label.getIsActive()) continue;
            LabelResponseDto labelResponseDto = LabelResponseDto.builder()
                    .labelId(label.getId())
                    .labelName(label.getName())
                    .build();
            labelResponseDtoList.add(labelResponseDto);
        }

        return SampleResponseDto.builder()
                .id(sample.getId())
                .sampleId(sample.getSampleDataId())
                .naturalQuestion(sample.getNaturalQuestion())
                .sqlQuery(sample.getSqlQuery())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(sample.getStatus())
                .updatedBy(userSimpleResponseDto)
                .labels(labelResponseDtoList)
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .build();
    }

    private Sample findSample(String id) {
        return sampleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Sample not found")
        );
    }

    private Map<String, Object> getSampleDataMap(String id) throws JsonProcessingException {
        Sample foundSample = findSample(id);
        Map<String, Object> sampleDataMap = objectMapper.readValue(foundSample.getSampleData(), Map.class);
        return sampleDataMap;
    }

    private Sample createUpdatedSample(Sample sample, User user,
                                       SampleStatus status, String sqlQuery,
                                       String naturalQuestion, List<String> labels) {
        return Sample.builder()
                .id(UUID.randomUUID().toString())
                .sampleDataId(sample.getSampleDataId())
                .naturalQuestion(naturalQuestion)
                .sqlQuery(sqlQuery)
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(status)
                .sampleData(sample.getSampleData())
                .group(sample.getGroup())
                .updatedBy(user.getId())
                .labels(labels)
                .build();
    }
}
