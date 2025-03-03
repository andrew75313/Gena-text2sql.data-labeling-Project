package org.example.datalabelingtool.domain.users.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.datalabelingtool.domain.groups.entity.Group;
import org.example.datalabelingtool.global.entity.Timestamp;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamp {

    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;
    @Column(nullable = false)
    private Boolean isActive;

    @ManyToMany(mappedBy = "reviewers")
    private List<Group> groups;

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}