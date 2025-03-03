package org.example.datalabelingtool.domain.samples.repository;

import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SampleRepository extends JpaRepository<Sample, String> {

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE s.status IN ('UPDATED','DELETED','CREATED') " +
            "AND s.version_id = (SELECT MAX(s2.version_id) FROM samples s2 WHERE JSON_UNQUOTE(JSON_EXTRACT(s2.sample_data, '$.id')) = JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id'))) " +
            "ORDER BY JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) ASC", nativeQuery = true)
    List<Sample> findLatestUpdatedSample();

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) = ?1 " +
            "AND s.status IN ('UPDATED', 'DELETED', 'CREATED') " +
            "ORDER BY s.version_id DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Sample> findLatestBySampleId(String sampleId);

    @Query(value = "SELECT s.* FROM samples s " +
            "WHERE s.status LIKE 'REQUESTED_%' " +
            "ORDER BY JSON_UNQUOTE(JSON_EXTRACT(s.sample_data, '$.id')) ASC, s.version_id ASC", nativeQuery = true)
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

    @Query(value = "SELECT * FROM samples " +
            "WHERE version_id = ?1 " +
            "AND id != ?2 " +
            "AND status != 'CREATED' " +
            "ORDER BY updated_at",
            nativeQuery = true)
    List<Sample> getOtherSamplesOfSameVersion(Long versionId, String sampleId);
}
