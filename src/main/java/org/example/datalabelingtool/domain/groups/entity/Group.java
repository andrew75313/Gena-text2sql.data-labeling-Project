package org.example.datalabelingtool.domain.groups.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.datalabelingtool.domain.samples.entity.Sample;
import org.example.datalabelingtool.domain.users.entity.User;
import org.example.datalabelingtool.global.entity.Timestamp;

import java.util.Set;

@Entity
@Table(name = "review_groups")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group extends Timestamp {

    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column
    private String description;
    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "id")
    private Set<Sample> samples;

    @ManyToMany
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> reviewers;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void addReviewer(User user) {
        this.reviewers.add(user);
    }

    public void removeReviewer(User user) {
        this.reviewers.remove(user);
    }

    public void addSample(Sample sample) {
        this.samples.add(sample);
    }

    public void removeSample(Sample sample) {
        this.samples.remove(sample);
    }
}
