package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT p FROM tblPassenger p WHERE p.ID = :passengerId")
    Passenger getPassengerById(long passengerId);

    @Query("SELECT p FROM tblPassenger p WHERE p.DNI = :passengerDNI")
    List<Passenger> getByDNI(String passengerDNI);

    @Query("SELECT f.id, p.ID, p.email, p.firstName, p.lastName, p.DNI, p.didCheckIn, f.origin, f.destination, f.flightDate, f.state, s.ID, s.number, s.letter " +
            "FROM tblOrder o " +
            "JOIN tblShoppingCartSeats scs ON o.shoppingCartID = scs.shoppingCartID " +
            "JOIN tblSeat s ON scs.seatID = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "JOIN tblPassenger p ON p.ID = s.passengerId " +
            "WHERE scs.shoppingCartID = :shoppingCartId ")
    List<Object[]> getPassengerBookedDetailsByShoppingCartId(@Param("shoppingCartId") long shoppingCartId);

    @Query("SELECT f.id, p.ID, p.email, p.firstName, p.lastName, p.DNI, p.didCheckIn, f.origin, f.destination, f.flightDate, f.state, s.ID, s.number, s.letter " +
            "FROM tblOrder o " +
            "JOIN tblShoppingCartSeats scs ON o.shoppingCartID = scs.shoppingCartID " +
            "JOIN tblSeat s ON scs.seatID = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "JOIN tblPassenger p ON p.ID = s.passengerId " +
            "WHERE scs.shoppingCartID = :shoppingCartId AND p.DNI = :passengerDNI")
    List<Object[]> getPassengerBookedDetailsByShoppingCartIdandOwnDNI(@Param("shoppingCartId") long shoppingCartId, @Param("passengerDNI") String passengerDNI);

    @Query("SELECT f.id, p.ID, p.email, p.firstName, p.lastName, p.DNI, p.didCheckIn, f.origin, f.destination, f.flightDate, f.state, s.ID, s.number, s.letter " +
            "FROM tblOrder o " +
            "JOIN tblShoppingCartSeats scs ON o.shoppingCartID = scs.shoppingCartID " +
            "JOIN tblSeat s ON scs.seatID = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "JOIN tblPassenger p ON p.ID = s.passengerId " +
            "WHERE scs.shoppingCartID = :shoppingCartId AND p.DNI = :passengerDNI AND s.ID = :seatId ")
    List<Object[]> getPassengerBookedDetailsByShoppingCartIdOwnDNIandSeatId(@Param("shoppingCartId") long shoppingCartId, @Param("passengerDNI") String passengerDNI, @Param("seatId") long seatId);


}
