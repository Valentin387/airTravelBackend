package com.laboratory.airlinebackend.controller.service;

import com.laboratory.airlinebackend.controller.DTO.ReserveFlightDTO;
import com.laboratory.airlinebackend.model.*;
import com.laboratory.airlinebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddShoppingCartItemService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShoppingCartSeatsRepository shoppingCartSeatsRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> add_item_to_cart(@RequestBody ReserveFlightDTO bookFlightDTO){
        List<Seat> seats0 = new ArrayList<>();
        seats0 = seatRepository.getSeatsByFlightId(bookFlightDTO.getFlightID());

        //I need only the setas with seatClass = reserveFlightDTO.getSeatClass()
        List<Seat> seats = new ArrayList<>();
        //filter seats0
        for (Seat seat : seats0) {
            if (seat.getSeatClass().equals(bookFlightDTO.getSeatClass())) {
                seats.add(seat);
            }
        }

        Optional<User> userOptional = userRepository.findById(bookFlightDTO.getUserID());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User existingUser = userOptional.get();
        ShoppingCart existingShoppingCart = shoppingCartRepository.findById(existingUser.getShoppingCartID()).get();

        Optional <Flight> flightOptinal = flightRepository.findById(bookFlightDTO.getFlightID());
        if (flightOptinal.isEmpty()) {
            return ResponseEntity.badRequest().body("Flight not found");
        }
        Flight existingFlight = flightOptinal.get();
        double UnitPrice;
        if (existingFlight.getCostByPersonOffer() != 0) {
            UnitPrice = existingFlight.getCostByPersonOffer();
        } else {
            UnitPrice = existingFlight.getCostByPerson();
        }

        for (int i = 0; i < bookFlightDTO.getSeatQuantity(); i++) {
            int randomIndex = (int) (Math.random() * seats.size());
            Seat seat = seats.get(randomIndex);

            ShoppingCartSeats shoppingCartSeats = ShoppingCartSeats.builder()
                    .seatID(seat.getID())
                    .shoppingCartID(existingUser.getShoppingCartID())
                    .unitPrice(UnitPrice)
                    .build();

            shoppingCartSeatsRepository.save(shoppingCartSeats);
            existingShoppingCart.setTotalAmount(existingShoppingCart.getTotalAmount() + UnitPrice);
            seats.remove(randomIndex);
        }
        existingShoppingCart.setQuantity(existingShoppingCart.getQuantity() + 1);
        shoppingCartRepository.save(existingShoppingCart);

        return ResponseEntity.ok("Flight added to user's cart successfully");
    }


}
