package org.example.datalabelingtool.domain.samples.repository;

import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SampleRepository extends JpaRepository<Sample, String> {

    @Query(value = "SELECT s FROM Sample s " +
            "WHERE s.status IN ('UPDATED', 'DELETED', 'CREATED') " +
            "AND s.versionId = (SELECT MAX(s2.versionId) FROM Sample s2 WHERE s2.sampleDataId = s.sampleDataId) " +
            "ORDER BY s.sampleDataId ASC")
    List<Sample> findLatestUpdatedSample();

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) = ?1 " +
            "AND s.status IN ('UPDATED', 'DELETED', 'CREATED') " +
            "ORDER BY s.version_id DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Sample> findLatestBySampleId(String sampleId);

    @Query("SELECT s FROM Sample s " +
            "WHERE s.status IN ('REQUESTED_UPDATE', 'REQUESTED_DELETE')" +
            "ORDER BY s.versionId ASC")
    List<Sample> findRequestedSample();

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) = ?1 " +
            "AND s.status LIKE 'REQUESTED_%' " +
            "And s.version_id = ?2", nativeQuery = true)
    List<Sample> findRequestedBySampleIdAndVersionId(String sampleId, Long versionId);

    @Query(value = "SELECT u.* " +
            "FROM users u " +
            "JOIN user_group ug ON u.id = ug.user_id " +
            "JOIN review_groups g ON ug.group_id = g.id " +
            "WHERE g.id = (SELECT s.group_id FROM samples s WHERE s.id = ?1 LIMIT 1)",
            nativeQuery = true)
    List<User> findUsersAssignedToSample(String sampleId);

    @Query("SELECT s FROM Sample s " +
            "WHERE s.versionId = :versionId " +
            "AND s.id = :sampleDataId " +
            "AND s.status != 'CREATED' " +
            "ORDER BY s.updatedAt")
    List<Sample> getOtherSamplesOfSameVersion(Long versionId, Long sampleDataId);

    @Query(value = """
    SELECT sub.*
    FROM (
        SELECT s.*, 
               ROW_NUMBER() OVER (
                   PARTITION BY JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) 
                   ORDER BY s.updated_at DESC, s.version_id DESC
               ) AS rn
        FROM samples s
        JOIN user_group ug ON ug.group_id = s.group_id
        WHERE ug.user_id = ?1
          AND (
              s.updated_by = ?1
              OR (COALESCE(s.updated_by, '') != ?1 AND s.status = 'CREATED')
          )
    ) sub
    WHERE sub.rn = 1
    ORDER BY CAST(JSON_UNQUOTE(JSON_EXTRACT(sub.sample_data, '$.id')) AS UNSIGNED) ASC
    """, nativeQuery = true)
    List<Sample> findAllByUserId(String userId);


    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE s.status IN ('UPDATED','DELETED','CREATED') " +
            "AND s.version_id = (SELECT MAX(s2.version_id) FROM samples s2 WHERE JSON_UNQUOTE(JSON_EXTRACT(s2.sample_data, '$.id')) = JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id'))) " +
            "AND s.dataset_name = ?1 " +
            "ORDER BY CAST(JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) AS UNSIGNED) ASC", nativeQuery = true)
    List<Sample> findLatestUpdatedSampleOfDataset(String datasetName);

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE s.dataset_name = ?1 " +
            "LIMIT 1", nativeQuery = true)
    List<Sample> findByDatasetName(String datasetName);
}
