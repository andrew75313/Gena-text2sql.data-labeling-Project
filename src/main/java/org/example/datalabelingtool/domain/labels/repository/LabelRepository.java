package org.example.datalabelingtool.domain.labels.repository;

import org.example.datalabelingtool.domain.labels.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, String> {
    Optional<Label> findByName(String labelName);
}
