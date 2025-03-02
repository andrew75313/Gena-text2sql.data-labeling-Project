package org.example.datalabelingtool.domain.groups.repository;

import org.example.datalabelingtool.domain.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {
    List<Group> findAllByIsActiveTrue();
    Optional<Group> findByIdAndIsActiveTrue(String id);
}
