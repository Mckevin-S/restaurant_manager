package com.example.BackendProject.repository;

import com.example.BackendProject.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDate(LocalDate date);
    List<Reservation> findByTelephoneContainingOrNomClientContainingIgnoreCase(String telephone, String nomClient);
}
