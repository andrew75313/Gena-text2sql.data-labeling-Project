package org.example.datalabelingtool.domain.labels.repository;

import org.example.datalabelingtool.domain.labels.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, String> {
    Optional<Label> findByName(String labelName);

    @Query(value = "SELECT * FROM labels WHERE is_active = true", nativeQuery = true)
    List<Label> findByIsActiveTrue();
}
