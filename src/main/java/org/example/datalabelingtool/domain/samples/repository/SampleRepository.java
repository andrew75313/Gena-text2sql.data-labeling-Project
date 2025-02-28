package org.example.datalabelingtool.domain.samples.repository;

import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SampleRepository extends JpaRepository<Sample, String> {

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.status')) = 'UPDATED' " +
            "AND s.version_id = (SELECT MAX(s2.version_id) FROM samples s2 WHERE JSON_UNQUOTE(JSON_EXTRACT(s2.sample_data, '$.id')) = JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id'))) " +
            "ORDER BY JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) ASC", nativeQuery = true)
    List<Sample> findLatestUpdatedSample();

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) = ?1 " +
            "ORDER BY s.version_id DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Sample> findLatestBySampleId(String sampleId);
}
