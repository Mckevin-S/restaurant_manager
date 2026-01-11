package com.example.BackendProject.services.implementations;

import com.example.BackendProject.dto.ReservationDto;
import com.example.BackendProject.entities.Reservation;
import com.example.BackendProject.entities.TableRestaurant;
import com.example.BackendProject.mappers.ReservationMapper;
import com.example.BackendProject.repository.ReservationRepository;
import com.example.BackendProject.repository.TableRestaurantRepository;
import com.example.BackendProject.services.interfaces.ReservationService;
import com.example.BackendProject.utils.LoggingUtils;
import com.example.BackendProject.utils.StatutReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImplementation implements ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImplementation.class);
    private final ReservationRepository reservationRepository;
    private final TableRestaurantRepository tableRepository;
    private final ReservationMapper reservationMapper;

    public ReservationServiceImplementation(ReservationRepository reservationRepository,
                                            TableRestaurantRepository tableRepository,
                                            ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public ReservationDto createReservation(ReservationDto reservationDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Création d'une réservation pour {}", context, reservationDto.getNomClient());
        Reservation reservation = reservationMapper.toEntity(reservationDto);
        if (reservationDto.getTableId() != null) {
            TableRestaurant table = tableRepository.findById(reservationDto.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table non trouvée"));
            reservation.setTable(table);
        }
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toDto(saved);
    }

    @Override
    public ReservationDto updateReservation(Long id, ReservationDto reservationDto) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Mise à jour de la réservation ID: {}", context, id);
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        existing.setNomClient(reservationDto.getNomClient());
        existing.setEmail(reservationDto.getEmail());
        existing.setTelephone(reservationDto.getTelephone());
        existing.setNombrePersonnes(reservationDto.getNombrePersonnes());
        existing.setDate(reservationDto.getDate());
        existing.setHeure(reservationDto.getHeure());
        existing.setNotes(reservationDto.getNotes());

        if (reservationDto.getTableId() != null) {
            TableRestaurant table = tableRepository.findById(reservationDto.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table non trouvée"));
            existing.setTable(table);
        } else {
            existing.setTable(null);
        }

        Reservation updated = reservationRepository.save(existing);
        return reservationMapper.toDto(updated);
    }

    @Override
    public void deleteReservation(Long id) {
        String context = LoggingUtils.getLogContext();
        logger.info("{} Suppression de la réservation ID: {}", context, id);
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDto getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByDate(LocalDate date) {
        return reservationRepository.findByDate(date).stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDto updateStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        reservation.setStatut(StatutReservation.valueOf(status));
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> searchReservations(String term) {
        return reservationRepository.findByTelephoneContainingOrNomClientContainingIgnoreCase(term, term).stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }
}
