package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
