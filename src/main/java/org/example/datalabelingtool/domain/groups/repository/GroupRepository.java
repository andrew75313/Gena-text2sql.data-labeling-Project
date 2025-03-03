package org.example.datalabelingtool.domain.groups.repository;

import org.example.datalabelingtool.domain.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {
    List<Group> findAllByIsActiveTrue();
    Optional<Group> findByIdAndIsActiveTrue(String id);

    @Query(value = "SELECT g.* FROM review_groups g " +
            "JOIN user_group ug ON g.id = ug.group_id " +
            "WHERE ug.user_id = ?1", nativeQuery = true)
    List<Group> findByUserId(String id);
}
