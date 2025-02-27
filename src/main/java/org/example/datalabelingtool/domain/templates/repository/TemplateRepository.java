package org.example.datalabelingtool.domain.templates.repository;

import org.example.datalabelingtool.domain.templates.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, String> {
    Template findByTemplateNo(Long templateNo);
}
