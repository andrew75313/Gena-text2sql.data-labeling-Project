package org.example.datalabelingtool.domain.groups.repository;

import org.example.datalabelingtool.domain.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {
    Optional<Group> findByIdAndIsActiveTrue(String id);
}
