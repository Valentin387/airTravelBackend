package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Reservation;
import com.laboratory.airlinebackend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    //get the reservations made by a user, grouped by flightID
    @Query("SELECT f.id " +
            "FROM tblReservation r " +
            "JOIN tblSeat s ON r.IDSeat = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "WHERE r.IDUser = :userId " +
            "GROUP BY f.id")
    List<Object[]> getGroupedReservationsByUserId(@Param("userId") long userId);

    @Query("SELECT r.reservationDate, r.expirationDate, " +
            "f.id, f.origin, f.destination, f.flightDate, f.state, f.costByPerson, f.costByPersonOffer " +
            "FROM tblReservation r " +
            "JOIN tblSeat s ON r.IDSeat = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "WHERE r.IDUser = :userId AND f.id = :flightId " +
            "GROUP BY f.id, r.reservationDate, r.expirationDate,  f.origin, f.destination, f.flightDate, f.state, f.costByPerson, f.costByPersonOffer")
    List<Object[]> getReservationsByUserId(@Param("userId") long userId, @Param("flightId") long flightId);

    //get the seats by userID and flightID
    @Query("SELECT s FROM tblSeat s " +
            "JOIN tblReservation r ON s.ID = r.IDSeat " +
            "WHERE r.IDUser = :userId AND s.flightId = :flightId")
    List<Seat> getSeatsByUserIdAndFlightId(@Param("userId") long userId, @Param("flightId") long flightId);

    //get the reservation by IDSeat
    @Query("SELECT r FROM tblReservation r WHERE r.IDSeat = :idSeat")
    Optional<Reservation> findByIDSeat(long idSeat);

}
