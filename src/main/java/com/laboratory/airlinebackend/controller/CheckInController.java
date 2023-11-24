package com.laboratory.airlinebackend.controller;
import com.laboratory.airlinebackend.controller.DTO.ConsultCheckInDTO;
import com.laboratory.airlinebackend.controller.DTO.SeatState;
import com.laboratory.airlinebackend.controller.service.EmailSenderService;
import com.laboratory.airlinebackend.controller.service.PdfGenerationService;
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
@RequestMapping("/api/check-in")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class CheckInController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private ShoppingCartSeatsRepository shoppingCartSeatsRepository;


    @GetMapping("/consult")
    public ResponseEntity<?> consult(
            @RequestParam String orderCOD,
            @RequestParam String passengerDNI
    ) {
        try{
            //get the order

            Optional<Order> OptionalOrder = orderRepository.findByCOD(orderCOD);
            if(OptionalOrder.isEmpty()){
                System.out.println("Order not found");
                return ResponseEntity.badRequest().body("Order not found");
            }
            Order order = OptionalOrder.get();

            //get the passengers, find by DNI
            List<Passenger> passengers = passengerRepository.getByDNI(passengerDNI);
            //get the first passenger (they are all the same person beneath)
            Passenger passenger = passengers.get(0);


            //get the user who made the order
            User user = userRepository.getUserById(order.getUserID());

            //get the order's shopping cart ID
            long shoppingCartId = order.getShoppingCartID();

            List<Object[]> results;
            List<ConsultCheckInDTO> passengersBookingDetailsDTO = new ArrayList<>();

            //check if the passenger is the owner of the order
            //compare the DNI's
            if(!passenger.getDNI().equals(user.getDNI())){
                //get only my passenger details for the flight I was included in, in the order
                results = passengerRepository.getPassengerBookedDetailsByShoppingCartIdandOwnDNI(shoppingCartId, passengerDNI);
            }else{
                //System.out.println("Passenger is the holder of the order");
                //get all the passengers included in the order
                results = passengerRepository.getPassengerBookedDetailsByShoppingCartId(shoppingCartId);
            }

            for (Object[] result : results) {
                //System.out.println("result: " + result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " " + result[5] + " " + result[6] + " " + result[7] + " " + result[8] + " " + result[9] + " " + result[10] + " " + result[11] + " " + result[12]);
                ConsultCheckInDTO passengerBookingDetails = ConsultCheckInDTO.builder()
                        .flightId((long) result[0])
                        .passengerId((long) result[1])
                        .email((String) result[2])
                        .firstName((String) result[3])
                        .lastName((String) result[4])
                        .DNI((String) result[5])
                        .didCheckIn((boolean) result[6])
                        .origin((String) result[7])
                        .destination((String) result[8])
                        .flightDate((Date) result[9])
                        .state((String) result[10])
                        .SeatId((long) result[11])
                        .SeatNumber((int) result[12])
                        .SeatLetter((char) result[13])
                        .build();
                passengersBookingDetailsDTO.add(passengerBookingDetails);
            }

            return ResponseEntity.ok(passengersBookingDetailsDTO);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/confirm-check-in")
    public ResponseEntity<?> confirmCheckIn(
            @RequestParam long seatID
    ) {
        try {
            //get the seat
            Optional<Seat> optionalSeat = seatRepository.findById(seatID);
            if(optionalSeat.isEmpty()){
                //System.out.println("Seat not found");
                return ResponseEntity.badRequest().body("Seat not found");
            }
            Seat seat = optionalSeat.get();

            //get the passenger by seat.getPassengerId()
            Optional<Passenger> optionalPassenger = passengerRepository.findById(seat.getPassengerId());
            if(optionalPassenger.isEmpty()){
                //System.out.println("Passenger not found");
                return ResponseEntity.badRequest().body("Passenger not found");
            }
            Passenger passenger = optionalPassenger.get();
            passenger.setDidCheckIn(true);
            passengerRepository.save(passenger);

            //--------------------Process of sending the PDF to the passenger's email--------------------
            //get the ShoppingCarId in tblShoppingCartSeats by seatID
            Optional<ShoppingCartSeats> optionalShoppingCartSeats = shoppingCartSeatsRepository.findBySeatID(seatID);
            if(optionalShoppingCartSeats.isEmpty()){
                //System.out.println("ShoppingCartSeats not found");
                return ResponseEntity.badRequest().body("ShoppingCartSeats not found");
            }
            ShoppingCartSeats shoppingCartSeats = optionalShoppingCartSeats.get();
            long shoppingCartId = shoppingCartSeats.getShoppingCartID();

            //get the object ConsultCheckInDTO for this specific seat confirmation
            List<Object[]> results;
            List<ConsultCheckInDTO> passengersBookingDetailsDTO = new ArrayList<>();
            results = passengerRepository.getPassengerBookedDetailsByShoppingCartIdOwnDNIandSeatId(shoppingCartId, passenger.getDNI(), seatID);
            for (Object[] result : results) {
                //System.out.println("result: " + result[0] + " " + result[1] + " " + result[2] + " " + result[3] + " " + result[4] + " " + result[5] + " " + result[6] + " " + result[7] + " " + result[8] + " " + result[9] + " " + result[10] + " " + result[11] + " " + result[12]);
                ConsultCheckInDTO passengerBookingDetails = ConsultCheckInDTO.builder()
                        .flightId((long) result[0])
                        .passengerId((long) result[1])
                        .email((String) result[2])
                        .firstName((String) result[3])
                        .lastName((String) result[4])
                        .DNI((String) result[5])
                        .didCheckIn((boolean) result[6])
                        .origin((String) result[7])
                        .destination((String) result[8])
                        .flightDate((Date) result[9])
                        .state((String) result[10])
                        .SeatId((long) result[11])
                        .SeatNumber((int) result[12])
                        .SeatLetter((char) result[13])
                        .build();
                passengersBookingDetailsDTO.add(passengerBookingDetails);
            }
            ConsultCheckInDTO passengerBookingDetails = passengersBookingDetailsDTO.get(0);

            //send this object to the emailSenderService
            byte[] pdfBytes = pdfGenerationService.generatePdf(passengerBookingDetails.toString());

            emailSenderService.sendEmailWithAttachment(passenger.getEmail(),"BOARDING PASS", "Check the attached PDF. " +
                    "Thanks for choosing AirTravel", "BoardingPass.pdf", pdfBytes);
            //--------------------End of process of sending the PDF to the passenger's email--------------------

            return ResponseEntity.ok("Check-in successful, E-MAIL SENT");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/fetch-seats")
    public ResponseEntity<?> fetchAllSeats(
            @RequestParam long flightID
    ){
        try{
            //get the flight by ID
            Optional<Flight> optionalFlight = flightRepository.findById(flightID);
            if(optionalFlight.isEmpty()){
                //System.out.println("Flight not found");
                return ResponseEntity.badRequest().body("Flight not found");
            }
            Flight flight = optionalFlight.get();

            //get all the seats
            List<Seat> seats = seatRepository.getAllSeatsByFlightId(flight.getId());
            return ResponseEntity.ok(seats);

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/change-seat")
    public ResponseEntity<?> changeSeat(
            @RequestParam long passengerID,
            @RequestParam long currentSeatID,
            @RequestParam long newSeatID
    ){
        try{

            //get the current Seat
            Optional<Seat> optionalSeat= seatRepository.findById(currentSeatID);
            if(optionalSeat.isEmpty()){
                return ResponseEntity.badRequest().body("current Seat not found");
            }
            Seat currentSeat = optionalSeat.get();

            //get the instance in tblShoppingCartSeats by seatID, in order to keep the semantic coherence
            Optional<ShoppingCartSeats> optionalShoppingCartSeats = shoppingCartSeatsRepository.findBySeatID(currentSeatID);
            if(optionalShoppingCartSeats.isEmpty()){
                return ResponseEntity.badRequest().body("ShoppingCartSeats not found");
            }
            ShoppingCartSeats shoppingCartSeats = optionalShoppingCartSeats.get();

            //get the new Seat
            Optional<Seat> optionalNewSeat= seatRepository.findById(newSeatID);
            if(optionalNewSeat.isEmpty()){
                return ResponseEntity.badRequest().body("new Seat not found");
            }
            Seat newSeat = optionalNewSeat.get();

            //get the passenger
            Optional<Passenger> optionalPassenger = passengerRepository.findById(passengerID);
            if(optionalPassenger.isEmpty()){
                return ResponseEntity.badRequest().body("Passenger not found");
            }
            Passenger passenger = optionalPassenger.get();

            //Process
            //book the new seat
            newSeat.setPassengerId(passenger.getID());
            newSeat.setState(SeatState.BOOKED.toString());
            seatRepository.save(newSeat);
            //update the reference to the new Seat in tblShoppingCartSeats
            shoppingCartSeats.setSeatID(newSeatID);
            shoppingCartSeatsRepository.save(shoppingCartSeats);
            //free the current seat
            currentSeat.setPassengerId(0);
            currentSeat.setState(SeatState.AVAILABLE.toString());
            seatRepository.save(currentSeat);

            //update the passenger
            passenger.setDidCheckIn(true);
            passengerRepository.save(passenger);

            return ResponseEntity.ok("Successful seat change");

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
