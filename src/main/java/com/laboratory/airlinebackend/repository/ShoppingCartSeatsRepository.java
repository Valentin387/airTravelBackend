package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Passenger;
import com.laboratory.airlinebackend.model.Seat;
import com.laboratory.airlinebackend.model.ShoppingCartSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartSeatsRepository extends JpaRepository<ShoppingCartSeats, Long> {

    //get the reservations made by a user, grouped by flightID
    @Query("SELECT f.id " +
            "FROM tblShoppingCartSeats scs " +
            "JOIN tblSeat s ON scs.seatID = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "WHERE scs.shoppingCartID = :ShoppingCartId " +
            "GROUP BY f.id")
    List<Object[]> getGroupedItemsByShoppingCartId(@Param("ShoppingCartId") long ShoppingCartId);

    @Query("SELECT f.id, f.origin, f.destination, f.flightDate, f.state, f.costByPerson, f.costByPersonOffer " +
            "FROM tblShoppingCartSeats scs " +
            "JOIN tblSeat s ON scs.seatID = s.ID " +
            "JOIN tblFlight f ON s.flightId = f.id " +
            "WHERE scs.shoppingCartID = :ShoppingCartId AND f.id = :flightId " +
            "GROUP BY f.id, f.origin, f.destination, f.flightDate, f.state, f.costByPerson, f.costByPersonOffer")
    List<Object[]> getItemsByShoppingCartId(@Param("ShoppingCartId") long ShoppingCartId, @Param("flightId") long flightId);

    //get the seats by userID and flightID
    @Query("SELECT s FROM tblSeat s " +
            "JOIN tblShoppingCartSeats scs ON s.ID = scs.seatID " +
            "WHERE scs.shoppingCartID = :ShoppingCartId AND s.flightId = :flightId")
    List<Seat> getSeatsByShoppingCartIdAndFlightId(@Param("ShoppingCartId") long ShoppingCartId, @Param("flightId") long flightId);


    //get the passengers by shopping cart ID and flight ID
    @Query("SELECT p FROM tblPassenger p " +
            "JOIN tblSeat s ON s.passengerId = p.ID " +
            "JOIN tblShoppingCartSeats scs ON s.ID = scs.seatID " +
            "WHERE scs.shoppingCartID = :ShoppingCartId AND s.flightId = :flightId")
    List<Passenger> getPassengersByShoppingCartIdAndFlightId(@Param("ShoppingCartId") long ShoppingCartId, @Param("flightId") long flightId);

    Optional <ShoppingCartSeats> findBySeatID(long seatID);

    @Query("SELECT s FROM tblShoppingCartSeats s WHERE s.shoppingCartID = :shoppingCartId")
    List<ShoppingCartSeats> getShoppingCartSeatsByShoppingCartId(@Param("shoppingCartId") long shoppingCartId);

}
