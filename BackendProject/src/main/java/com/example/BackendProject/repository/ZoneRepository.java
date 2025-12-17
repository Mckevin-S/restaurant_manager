package com.example.BackendProject.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.BackendProject.entities.Zone;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByName(String name);
    @Query("SELECT z FROM Zone z WHERE z.restaurant.id = :idRestaurant")
    List<Zone> findZonesByRestaurantId(Long idRestaurant);
    Zone getZonesById(Long id);
}
