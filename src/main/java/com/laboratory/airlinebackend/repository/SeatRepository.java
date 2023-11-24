package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM tblSeat s " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "WHERE f.id = :id AND s.state = 'AVAILABLE' ")
    List<Seat> getSeatsByFlightId(Long id);

    @Query("SELECT s FROM tblSeat s " +
            "WHERE s.ID = :id ")
    Seat getSeatById(Long id);

    @Query("SELECT s FROM tblSeat s " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "WHERE f.id = :id ")
    List<Seat> getAllSeatsByFlightId(Long id);
}
