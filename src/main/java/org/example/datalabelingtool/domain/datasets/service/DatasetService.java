package org.example.datalabelingtool.domain.datasets.service;

import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.datalabelingtool.domain.datasets.dto.DatasetMetadataDto;
import org.example.datalabelingtool.domain.samples.dto.SampleResponseDto;
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

    @Transactional
    public void uploadCsvFile(MultipartFile file, DatasetMetadataDto metadata) throws Exception {
        String datasetName = metadata.getDatasetName();
        String datasetDescription = metadata.getDatasetDescription();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] columns = csvReader.readNext();

            if (columns == null) throw new FileProcessingException("CSV file is empty");

            String[] targetColumns = {"sql_query", "natural_question", "no_sql_template", "sql_template"};
            Map<String, Integer> columnIndexMap = new HashMap<>();

            for (int i = 0; i < columns.length; i++) {
                for (String column : targetColumns) {
                    if (columns[i].equalsIgnoreCase(column)) {
                        columnIndexMap.put(column, i);
                    }
                }
            }

            if (columnIndexMap.size() != 4)
                throw new FileProcessingException("CSV file must contain 4 columns : sql_query, natural_question, no_template, sql_template");

            Integer noTemplateIndex = columnIndexMap.get("no_sql_template");
            Integer sqlTemplateIndex = columnIndexMap.get("sql_template");

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
        Sample sample = sampleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Sample not found")
        );

        return toSampleResponseDto(sample);
    }

    public DataResponseDto getLatestUpdatesSamples() {
        List<SampleResponseDto> responseDtoList = sampleRepository.findLatestUpdatedSample().stream()
                .map(this::toSampleResponseDto)
                .collect(Collectors.toList());

        return new DataResponseDto(responseDtoList);
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
}
