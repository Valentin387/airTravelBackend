package com.laboratory.airlinebackend.controller;

import com.laboratory.airlinebackend.controller.DTO.ReserveFlightDTO;
import com.laboratory.airlinebackend.controller.DTO.ShoppingCartSeatsDetailsDTO;
import com.laboratory.airlinebackend.controller.service.AddShoppingCartItemService;
import com.laboratory.airlinebackend.controller.service.DropShoppingCartItemService;
import com.laboratory.airlinebackend.model.*;
import com.laboratory.airlinebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/shopping-cart")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class ShoppingCartController {
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

    @Autowired
    private AddShoppingCartItemService addShoppingCartItemService;

    @Autowired
    private DropShoppingCartItemService dropShoppingCartItemService;


    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addFlightToCart(
            @RequestBody ReserveFlightDTO bookFlightDTO
    ){
        try {
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

            Optional <Flight> flightOptional = flightRepository.findById(bookFlightDTO.getFlightID());
            if (flightOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Flight not found");
            }
            Flight existingFlight = flightOptional.get();
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
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding flight to cart");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getItemsByUserID(
            @RequestParam long userID
    ){
        try{
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

            List<Object[]> results = shoppingCartSeatsRepository.getGroupedItemsByShoppingCartId(shoppingCart.getID());
            //for every object in results, call the getItemsByShoppingCartId method

            List<ShoppingCartSeatsDetailsDTO> items = new ArrayList<>();
            for (Object[] result : results) {
                List<Object[]> results2 = shoppingCartSeatsRepository.getItemsByShoppingCartId(shoppingCart.getID(), (long) result[0]);
                for (Object[] result2 : results2) {
                    ShoppingCartSeatsDetailsDTO shoppingCartSeatsDetailsDTO = ShoppingCartSeatsDetailsDTO.builder()
                            .flightId((Long) result2[0])
                            .origin((String) result2[1])
                            .destination((String) result2[2])
                            .flightDate((Date) result2[3])
                            .state((String) result2[4])
                            .costByPerson((Double) result2[5])
                            .costByPersonOffer((Double) result2[6])
                            .seats(shoppingCartSeatsRepository.getSeatsByShoppingCartIdAndFlightId(shoppingCart.getID(), (long) result2[0]))
                            .build();
                    items.add(shoppingCartSeatsDetailsDTO);
                }
            }

            return ResponseEntity.ok(items);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting shopping cart items");
        }
    }

    @DeleteMapping("/drop")
    public ResponseEntity<?> dropItemFromShoppingCart(
            @RequestParam  long userID,
            @RequestParam  long flightID
    ){
        try {
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
                Optional<ShoppingCartSeats> OptionalShoppingCartSeats = shoppingCartSeatsRepository.findBySeatID(seat.getID());
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

        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error dropping shopping cart items");
        }
    }

    @GetMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestParam long userID
    ){
        try {
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

            return ResponseEntity.ok(shoppingCart);

        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking out shopping cart");
        }
    }

    @PostMapping("/modify-cart-item")
    public ResponseEntity<?> addSingleSeatToCart(
            @RequestBody ReserveFlightDTO bookFlightDTO
    ){
        try {
            //I call the service DropShoppingCartItemService and then, the service AddShoppingCartItemService
            dropShoppingCartItemService.drop_item_from_cart(bookFlightDTO.getUserID(), bookFlightDTO.getFlightID());
            addShoppingCartItemService.add_item_to_cart(bookFlightDTO);

            return ResponseEntity.ok("Flight modified in user's cart successfully");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error modifying flight in cart");
        }
    }



}
