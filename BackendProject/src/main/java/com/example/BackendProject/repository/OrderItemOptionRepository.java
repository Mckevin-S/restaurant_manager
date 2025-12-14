package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.OrderItemOption;

@Repository
public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {
}
