package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.PlatOption;
import com.example.BackendProject.entities.PlatOptionPK;

@Repository
public interface PlatOptionRepository extends JpaRepository<PlatOption, PlatOptionPK> {

}
