package org.example.datalabelingtool.domain.users.repository;

import org.example.datalabelingtool.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
