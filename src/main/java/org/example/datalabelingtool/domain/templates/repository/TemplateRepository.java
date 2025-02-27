package org.example.datalabelingtool.domain.templates.repository;

import org.example.datalabelingtool.domain.templates.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, String> {
    Template findByTemplateNo(Long templateNo);

    @Query(value = "SELECT t.* FROM templates t ORDER BY t.template_no ASC", nativeQuery = true)
    List<Template> findAllOrderByTemplateNoAsc();
}
