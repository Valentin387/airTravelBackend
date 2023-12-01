package com.laboratory.airlinebackend.controller.service;

import com.laboratory.airlinebackend.model.*;
import com.laboratory.airlinebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DropShoppingCartItemService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private ShoppingCartSeatsRepository shoppingCartSeatsRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> drop_item_from_cart(
            @RequestParam long userID,
            @RequestParam  long flightID
    ){
        //get the user
        Optional<User> OptionalUser = userRepository.findById(userID);
        if (OptionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = OptionalUser.get();

        //get the shopping cart
        Optional<ShoppingCart> OptionalShoppingCart = shoppingCartRepository.findById(user.getShoppingCartID());
        if (OptionalShoppingCart.isEmpty()) {
            return ResponseEntity.badRequest().body("Shopping cart not found");
        }
        ShoppingCart shoppingCart = OptionalShoppingCart.get();

        //get the flight
        Optional<Flight> OptionalFlight = flightRepository.findById(flightID);
        if (OptionalFlight.isEmpty()) {
            return ResponseEntity.badRequest().body("Flight not found");
        }
        Flight flight = OptionalFlight.get();

        //get the seats
        List<Seat> seats = shoppingCartSeatsRepository.getSeatsByShoppingCartIdAndFlightId(shoppingCart.getID(), flightID);

        //delete the shopping cart seats
        for (Seat seat : seats) {
            Optional<ShoppingCartSeats> OptionalShoppingCartSeats = shoppingCartSeatsRepository.findBySeatIDandShoppingCartID(seat.getID(), shoppingCart.getID());
            if (OptionalShoppingCartSeats.isEmpty()) {
                return ResponseEntity.badRequest().body("Shopping cart seats not found");
            }
            ShoppingCartSeats shoppingCartSeats = OptionalShoppingCartSeats.get();
            shoppingCartSeatsRepository.delete(shoppingCartSeats);

            //update the shopping cart
            if (flight.getCostByPersonOffer() != 0) {
                shoppingCart.setTotalAmount(shoppingCart.getTotalAmount() - flight.getCostByPersonOffer());
            } else {
                shoppingCart.setTotalAmount(shoppingCart.getTotalAmount() - flight.getCostByPerson());
            }

        }
        shoppingCart.setQuantity(shoppingCart.getQuantity() - 1);
        shoppingCartRepository.save(shoppingCart);

        return ResponseEntity.ok("Shopping cart item dropped successfully");
    }
}
