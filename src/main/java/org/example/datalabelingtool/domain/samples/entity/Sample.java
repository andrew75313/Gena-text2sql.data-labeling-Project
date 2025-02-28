package org.example.datalabelingtool.domain.samples.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.datalabelingtool.global.entity.Timestamp;

@Entity
@Table(name = "samples")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sample extends Timestamp {
    @Id
    private String id;

    @Column(nullable = false)
    private String datasetName;

    private String datasetDescription;

    @Column(nullable = false)
    private Long versionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SampleStatus status;

    @Column(columnDefinition = "JSON")
    private String sampleData;

    public void updateStatus(SampleStatus status) {
        this.status = status;
    }
}
