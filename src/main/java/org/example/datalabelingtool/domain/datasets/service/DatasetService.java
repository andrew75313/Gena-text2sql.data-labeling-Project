package org.example.datalabelingtool.domain.datasets.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.datalabelingtool.domain.datasets.dto.DatasetMetadataDto;
import org.example.datalabelingtool.domain.datasets.entity.DatasetColumn;
import org.example.datalabelingtool.domain.samples.dto.SampleResponseDto;
import org.example.datalabelingtool.domain.samples.dto.SampleUpdateRequestDto;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.samples.entity.SampleStatus;
import org.example.datalabelingtool.domain.samples.repository.SampleRepository;
import org.example.datalabelingtool.domain.templates.entity.Template;
import org.example.datalabelingtool.domain.templates.repository.TemplateRepository;
import org.example.datalabelingtool.global.dto.DataResponseDto;
import org.example.datalabelingtool.global.exception.FileProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatasetService {

    private final SampleRepository sampleRepository;
    private final TemplateRepository templateRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void uploadCsvFile(MultipartFile file, DatasetMetadataDto metadata) throws Exception {
        String datasetName = metadata.getDatasetName();
        String datasetDescription = metadata.getDatasetDescription();

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

    public SampleResponseDto getSampleById(String id) {
        return toSampleResponseDto(findSample(id));
    }

    public DataResponseDto getLatestUpdatesSamples() {
        List<SampleResponseDto> responseDtoList = sampleRepository.findLatestUpdatedSample().stream()
                .map(this::toSampleResponseDto)
                .collect(Collectors.toList());

        return new DataResponseDto(responseDtoList);
    }

    @Transactional
    public SampleResponseDto updateSample(String id, SampleUpdateRequestDto requestDto) throws JsonProcessingException {
        String sqlQuery = requestDto.getSqlQuery();
        String naturalQuestion = requestDto.getNaturalQuestion();
        Boolean passed = requestDto.getPassed();
        Boolean deleted = requestDto.getDeleted();

        Sample sample = findSample(id);
        Long versionId = sample.getVersionId();
        String sampleData = sample.getSampleData();
        Map<String, Object> sampleDataMap = objectMapper.readValue(sampleData, Map.class);

        String sampleId = (String) sampleDataMap.get("id");
        Sample latestSample = sampleRepository.findLatestBySampleId(sampleId).orElseThrow(
                () -> new IllegalArgumentException("Bad Request")
        );

        if (latestSample.getVersionId() > versionId) throw new IllegalArgumentException("Higher Version exists");

        if (passed && deleted)
            throw new IllegalArgumentException("Passed and Deleted can't be requested at the same time");

        if (passed || deleted) {
            if (sqlQuery != null && naturalQuestion != null)
                throw new IllegalArgumentException("SQL Query or Natural Question can't be requested when Passed or Deleted are requested");
        }

        if (sqlQuery != null) {
            if (sampleDataMap.containsKey(DatasetColumn.SQL_QUERY.toString())) {
                sampleDataMap.put(DatasetColumn.SQL_QUERY.toString(), sqlQuery);
                sample.updateStatus(SampleStatus.REQUESTED_UPDATE);
            }
        }

        if (naturalQuestion != null) {
            if (sampleDataMap.containsKey(DatasetColumn.NATURAL_QUESTION.toString())) {
                sampleDataMap.put(DatasetColumn.NATURAL_QUESTION.toString(), naturalQuestion);
                sample.updateStatus(SampleStatus.REQUESTED_UPDATE);
            }
        }

        sample = Sample.builder()
                .id(UUID.randomUUID().toString())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId() + 1)
                .sampleData(objectMapper.writeValueAsString(sampleDataMap))
                .status(sample.getStatus())
                .build();

        if (passed) sample.updateStatus(SampleStatus.REQUESTED_UPDATE);
        if (deleted) sample.updateStatus(SampleStatus.REQUESTED_UPDATE);

        sampleRepository.save(sample);

        return toSampleResponseDto(sample);
    }

    private SampleResponseDto toSampleResponseDto(Sample sample) {
        return SampleResponseDto.builder()
                .id(sample.getId())
                .datasetName(sample.getDatasetName())
                .datasetDescription(sample.getDatasetDescription())
                .versionId(sample.getVersionId())
                .status(sample.getStatus())
                .sampleData(sample.getSampleData())
                .createdAt(sample.getCreatedAt())
                .updatedAt(sample.getUpdatedAt())
                .build();
    }

    private Sample findSample(String id) {
        return sampleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Sample not found")
        );
    }
}
