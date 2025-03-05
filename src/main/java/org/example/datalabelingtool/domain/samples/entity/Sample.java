package org.example.datalabelingtool.domain.samples.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.global.entity.Timestamp;
import org.example.datalabelingtool.global.util.ListToJsonConverter;

import java.util.List;

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

    @Column(columnDefinition = "JSON")
    @Convert(converter = ListToJsonConverter.class)
    private List<String> labels;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public void updateStatus(SampleStatus status) {
        this.status = status;
    }

    public void updateVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public void updateLabels (List<String> labels) {
        this.labels = labels;
    }

    public void updateGroup(Group newGroup) {
        if (this.group != null) {
            this.group.getSamples().remove(this);
        }

        this.group = newGroup;

        if (newGroup != null) {
            newGroup.getSamples().add(this);
        }
    }
}
