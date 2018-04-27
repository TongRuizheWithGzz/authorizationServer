package com.oweu.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.oweu.authorizationserver.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
