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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        if(!sampleRepository.findByDatasetName(datasetName).isEmpty()) throw new IllegalArgumentException("Dataset already exists");

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

            if (columnIndexMap.size() != 4)
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
                        .datasetName(datasetName)
                        .datasetDescription(datasetDescription)
                        .versionId(1L)
                        .status(SampleStatus.CREATED)
                        .sampleData(sampleData.toString())
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

        List<SampleResponseDto> otherVersionsSamples = sampleRepository.getOtherSamplesOfSameVersion(sample.getVersionId(), sample.getId())
                .stream()
                .map(this::toSampleResponseDto)
                .collect(Collectors.toList());

        return SampleSameVerResponseDto.builder()
                .id(sample.getId())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(sample.getStatus())
                .sampleData(sample.getSampleData())
                .updatedBy(sample.getUpdatedBy())
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .otherVersionsSamples(otherVersionsSamples)
                .build();
    }

    public DataResponseDto getLatestUpdatesSamples() {
        List<SampleResponseDto> responseDtoList = sampleRepository.findLatestUpdatedSample().stream()
                .map(this::toSampleResponseDto)
                .collect(Collectors.toList());

        return new DataResponseDto(responseDtoList);
    }

    public DataResponseDto getRequestedSamples() {
        List<SampleResponseDto> responseDtoList = sampleRepository.findRequestedSample().stream()
                .map(this::toSampleResponseDto)
                .collect(Collectors.toList());

        return new DataResponseDto(responseDtoList);
    }

    @Transactional
    public SampleResponseDto updateSample(String id, SampleUpdateRequestDto requestDto) throws JsonProcessingException {
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
        Map<String, Object> sampleDataMap = getSampleDataMap(sample.getId());
        String sampleId = (String) sampleDataMap.get("id");

        Sample latestSample = sampleRepository.findLatestBySampleId(sampleId).orElseThrow(
                () -> new IllegalArgumentException("Bad Request")
        );

        if (latestSample.getVersionId() > versionId) throw new IllegalArgumentException("Higher Version exists");

        if (passed && deleted)
            throw new IllegalArgumentException("Passed and Deleted can't be requested at the same time");

        if (passed || deleted) {
            if (!sqlQuery.isEmpty() || !naturalQuestion.isEmpty() || !labels.isEmpty())
                throw new IllegalArgumentException("SQL Query or Natural Question can't be requested when Passed or Deleted are requested");
        }

        if (!passed && !deleted && sqlQuery.isEmpty() && naturalQuestion.isEmpty() && labels.isEmpty())
            throw new IllegalArgumentException("No update requested");

        SampleStatus updatedStatus = sample.getStatus();

        if (!sqlQuery.isEmpty()) {
            if (sampleDataMap.containsKey(DatasetColumn.SQL_QUERY.toString())) {
                sampleDataMap.put(DatasetColumn.SQL_QUERY.toString(), sqlQuery);
                updatedStatus = SampleStatus.REQUESTED_UPDATE;
            }
        }

        if (!naturalQuestion.isEmpty()) {
            if (sampleDataMap.containsKey(DatasetColumn.NATURAL_QUESTION.toString())) {
                sampleDataMap.put(DatasetColumn.NATURAL_QUESTION.toString(), naturalQuestion);
                updatedStatus = SampleStatus.REQUESTED_UPDATE;
            }
        }

        if (passed) updatedStatus = SampleStatus.REQUESTED_UPDATE;
        if (deleted) updatedStatus = SampleStatus.REQUESTED_DELETE;

        List<String> updatedLabels = new ArrayList<>();
        for (String labelId : labels) {
            Label foundLabel = labelRepository.findById(labelId).orElse(null);
            if (foundLabel == null) {
                continue;
            } else {
                updatedLabels.add(foundLabel.getId());
            }
        }

        Sample updatedSample = Sample.builder()
                .id(UUID.randomUUID().toString())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(updatedStatus)
                .sampleData(objectMapper.writeValueAsString(sampleDataMap))
                .group(sample.getGroup())
                .updatedBy(user.getId())
                .labels(updatedLabels)
                .build();

        sampleRepository.save(updatedSample);

        sample.getGroup().getSamples().add(updatedSample);

        Sample responseSample = findSample(updatedSample.getId());

        return toSampleResponseDto(responseSample);
    }

    @Transactional
    public SampleApproveResponseDto approveSample(@Valid String id) throws JsonProcessingException {
        Sample sample = findSample(id);
        sample.updateStatus(SampleStatus.UPDATED);

        Map<String, Object> sampleDataMap = getSampleDataMap(sample.getId());
        String sampleId = (String) sampleDataMap.get("id");

        List<Sample> sampleList = sampleRepository.findRequestedBySampleIdAndVersionId(sampleId, sample.getVersionId());
        for (Sample requestedSample : sampleList) {
            requestedSample.updateStatus(SampleStatus.REJECTED);
            requestedSample.updateVersionId(requestedSample.getVersionId() + 1);
        }

        sample.updateVersionId(sample.getVersionId() + 1);

        return SampleApproveResponseDto.builder()
                .sampleId(sample.getId())
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

        if (sample.getUpdatedBy() != null) {
            User user = userRepository.findById(sample.getUpdatedBy()).orElse(null);

            userSimpleResponseDto = UserSimpleResponseDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();
        }

        List<LabelResponseDto> labelResponseDtoList = new ArrayList<>();
        for(String labelId : sample.getLabels()) {
            Label label = labelRepository.findById(labelId).orElse(null);
            LabelResponseDto labelResponseDto = LabelResponseDto.builder()
                    .labelId(label.getId())
                    .labelName(label.getName())
                    .build();
            labelResponseDtoList.add(labelResponseDto);
        }

        return SampleResponseDto.builder()
                .id(sample.getId())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(sample.getStatus())
                .sampleData(sample.getSampleData())
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


}
