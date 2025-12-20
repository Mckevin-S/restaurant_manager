package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Restaurant;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findRestaurantById(Long id);
    List<Restaurant> findRestaurantByNom(String nom);
    List<Restaurant> findRestaurantByAdresse(String address);


}
