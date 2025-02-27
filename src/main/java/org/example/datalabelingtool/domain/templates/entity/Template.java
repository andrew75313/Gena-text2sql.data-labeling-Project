package org.example.datalabelingtool.domain.templates.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.datalabelingtool.global.entity.Timestamp;

@Entity
@Table(name = "templates")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template extends Timestamp {
    @Id
    private String id;

    @Column(nullable = false)
    private Long templateNo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
