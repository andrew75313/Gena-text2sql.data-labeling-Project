package org.example.datalabelingtool.domain.users.repository;

import org.example.datalabelingtool.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByIdAndIsActiveTrue(String id);
    List<User> findAllByIsActiveTrue();
}
