package com.example.BackendProject.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Restaurant;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findRestaurantById(Long id);
<<<<<<< HEAD
=======
    List<Restaurant> findRestaurantByName(String name);
    List<Restaurant> findRestaurantByAddress(String address);
>>>>>>> 689ba581af429fb5c070ecceb76d174729a2ecd8


}
