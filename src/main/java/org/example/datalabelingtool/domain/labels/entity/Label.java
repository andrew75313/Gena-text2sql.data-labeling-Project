package org.example.datalabelingtool.domain.labels.entity;

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
@Table(name = "lables")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Label extends Timestamp {
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private Boolean isActive;

    public void updateLabel(String name) {
        this.name = name;
    }
}
