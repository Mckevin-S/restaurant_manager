package com.example.BackendProject.services.interfaces;

import com.example.BackendProject.dto.ReservationDto;
import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    ReservationDto createReservation(ReservationDto reservationDto);
    ReservationDto updateReservation(Long id, ReservationDto reservationDto);
    void deleteReservation(Long id);
    ReservationDto getReservationById(Long id);
    List<ReservationDto> getAllReservations();
    List<ReservationDto> getReservationsByDate(LocalDate date);
    ReservationDto updateStatus(Long id, String status);
    List<ReservationDto> searchReservations(String term);
}
