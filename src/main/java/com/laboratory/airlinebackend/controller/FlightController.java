package com.laboratory.airlinebackend.controller;
import com.laboratory.airlinebackend.controller.DTO.FlightState;
import com.laboratory.airlinebackend.controller.DTO.RegisterRequestFlight;
import com.laboratory.airlinebackend.controller.DTO.*;
import com.laboratory.airlinebackend.controller.service.SeatCreatorService;
import com.laboratory.airlinebackend.model.*;
import com.laboratory.airlinebackend.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;


@RestController
@RequestMapping("/api/flight")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatCreatorService seatCreatorService;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private SeatRepository seatRepository;


    @PostMapping("/create")
    public ResponseEntity<?> createNewFlight(
            @RequestBody RegisterRequestFlight requestNewFlight
    ){
        var currentDate = new java.util.Date();
        if (requestNewFlight.getFlightDate().before(currentDate)) {
            return ResponseEntity.badRequest().body("Flight date must be after current date");
        }
        try{
            //all combinations of travels are listed here below (not scalable I know, but works)
            Map<String, String> cityToCountryMap;
            cityToCountryMap = new HashMap<>();
            cityToCountryMap.put("Madrid", "Spain");
            cityToCountryMap.put("Londres", "United Kingdom");
            cityToCountryMap.put("New York", "United States");
            cityToCountryMap.put("Buenos Aires", "Argentina");
            cityToCountryMap.put("Miami", "United States");
            cityToCountryMap.put("Pereira", "Colombia");
            cityToCountryMap.put("Bogotá", "Colombia");
            cityToCountryMap.put("Medellín", "Colombia");
            cityToCountryMap.put("Cali", "Colombia");
            cityToCountryMap.put("Cartagena", "Colombia");

            int assignedSeats;
            int firstClassSeatsQuantity;
            int economicClassSeatsQuantity;
            int firstClassSeatRows;
            int economicClassSeatRows;
            //System.out.println(requestNewFlight);
            String country1 = cityToCountryMap.get(requestNewFlight.getOrigin());
            String country2 = cityToCountryMap.get(requestNewFlight.getDestination());
            boolean isInternational = !country1.equals(country2);

            if (isInternational) {
                //International
                assignedSeats = 250;
                firstClassSeatsQuantity = 50;
                firstClassSeatRows = 9; //round up firstClassSeatsQuantity/6
                economicClassSeatsQuantity = 200;
                economicClassSeatRows = 34; //round up economicClassSeatsQuantity/6
            } else {
                //National
                assignedSeats = 150;
                firstClassSeatsQuantity = 25;
                firstClassSeatRows = 5; //round up firstClassSeatsQuantity/6
                economicClassSeatsQuantity = 125;
                economicClassSeatRows = 21; //round up economicClassSeatsQuantity/6
            }
            var flight = Flight.builder()
                .flightDate(requestNewFlight.getFlightDate())
                .origin(requestNewFlight.getOrigin())
                .destination(requestNewFlight.getDestination())
                .flightDuration(requestNewFlight.getFlightDuration())
                .arrivalDate(requestNewFlight.getArrivalDate())
                .costByPerson(requestNewFlight.getCostByPerson())
                .isInternational(isInternational)
                .state(FlightState.ON_TIME.toString())
                .availableSeats(assignedSeats)
                .offerID(0L)
                .build();

            // Check for valid offers
            List<Offer> validOffers = offerRepository.findValidOffers(
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getFlightDate()
            );

            if (validOffers.size() > 0) {
                flight.setCostByPersonOffer(
                        // Multiply by 100 to keep two decimal places, then round, and divide by 100
                        Math.round(
                                (flight.getCostByPerson() - ( validOffers.get(0).getDiscount()/100 * flight.getCostByPerson() ))
                                * 100.0
                        )/100.0
                );
                flight.setOfferID(validOffers.get(0).getID());
            }
            flightRepository.save(flight);

            //I'm going to fetch the seats whose flightID is 0
            //then I{m going to count if there are enough seats for the new flight depending on whether it is international or not
            //if there are enough seats, I'm going to assign them to the new flight
            //if there are not enough seats, I'm going to create the seats for the new flight
            List<Seat> seats0 = new ArrayList<>();
            seats0 = seatRepository.getSeatsByFlightId(0L);
            //count the elements in seats0
            int countSeats0 = 0;
            int economicClassSeatsQuantity0 = 0;
            int firstClassSeatsQuantity0 = 0;
            for (Seat seat : seats0) {
                if (seat.getSeatClass().equals("Economic Class"))
                    economicClassSeatsQuantity0++;
                else
                    firstClassSeatsQuantity0++;
                countSeats0++;
            }
            System.out.println("countSeats0: " + countSeats0);
            System.out.println("economicClassSeatsQuantity0: " + economicClassSeatsQuantity0);
            System.out.println("firstClassSeatsQuantity0: " + firstClassSeatsQuantity0);

            if (countSeats0 >= assignedSeats && economicClassSeatsQuantity0 >= economicClassSeatsQuantity && firstClassSeatsQuantity0 >= firstClassSeatsQuantity) {
                //there are enough seats
                System.out.println("there are enough seats");
                //I'm going to assign them to the new flight, respecting the classes and the quantity
                int countEconomic = 0;
                int countFirstClass = 0;
                for (Seat seat : seats0) {
                    if (seat.getSeatClass().equals("Economic Class") && countEconomic < economicClassSeatsQuantity) {
                        seat.setFlightId(flight.getId());
                        seatRepository.save(seat);
                        countEconomic++;
                    } else if (seat.getSeatClass().equals("First Class") && countFirstClass < firstClassSeatsQuantity) {
                        seat.setFlightId(flight.getId());
                        seatRepository.save(seat);
                        countFirstClass++;
                    }
                }

            } else {
                //there are not enough seats
                System.out.println("there are no enough seats");
                //I'm going to create the seats for the new flight
                seatCreatorService.create_seats(
                        //assignedSeats,
                        firstClassSeatsQuantity,
                        firstClassSeatRows,
                        economicClassSeatsQuantity,
                        economicClassSeatRows,
                        flight);
            }

            return ResponseEntity.ok("Flight created succesfully \n" +
                    "added economic class seats: " + economicClassSeatsQuantity + "\n" +
                    "added first class seats: " + firstClassSeatsQuantity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating flight");
        }

    }

    @GetMapping("/list/active")
    public ResponseEntity<?> getActiveFlights(){
        try{
            List<Flight> flights = flightRepository.getOnTimeFlights();
            return ResponseEntity.ok(flights);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting active flights");
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> flightDetail (@PathVariable Long id){
        try{
            Optional<Flight> flightOptional = flightRepository.findById(id);
            if (flightOptional.isPresent()) {
                Flight flight = flightOptional.get();
                return ResponseEntity.ok(flight);
            } else {
                return ResponseEntity.badRequest().body("Flight not found");
            }
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting flight detail");
        }
    }


    @GetMapping("/list/{state}")
    public ResponseEntity<?> getFlightsByState(@PathVariable String state) {
        try{
            List<Flight> flights = flightRepository.getFlightsByState(state);

            // Si la lista de vuelos es nula o está vacía,se devulve un ResponseEntity.notFound()
            if (flights == null || flights.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            // En caso de error, devolver un ResponseEntity con el mensaje de error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener la lista de vuelos");
        }

    }

    @PostMapping ("/delete/{id}")
    public ResponseEntity<?> flightCancelled(@PathVariable Long id) {
        try{
            // Obtener el vuelo por ID
            Optional<Flight> flightOptional = flightRepository.findById(id);

            if(flightOptional.isPresent()) {
                Flight flight = flightOptional.get();

                // Verificar si el vuelo existe y está activo
                if (!"ON_TIME".equals(flight.getState())) {
                    return ResponseEntity.notFound().build();
                }

                flight.setState(FlightState.CANCELLED.toString());
                flight.setAvailableSeats(0);
                //update the flight with the new state in the database
                flightRepository.save(flight);

                // Actualizar las sillas asociadas al vuelo borrado
                List<Seat> seats = seatRepository.getSeatsByFlightId(id);
                //seats.forEach(seat -> seat.setFlightId(0));
                for (Seat seat : seats) {
                    seat.setFlightId(0);
                    seatRepository.save(seat);
                }

                //change state to cancelled

                //flightRepository.deleteById(id);

                return ResponseEntity.ok("Vuelo con ID " + id + " cancelado exitosamente /n");
            }else {
                // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
                return ResponseEntity.notFound().build();
            }
        }catch (Exception e) {
            e.printStackTrace(); // Imprime el seguimiento de la pila en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cancelar el vuelo");
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editFlight(@PathVariable Long id, @RequestBody EditFlight flightUpdated) {
        Optional<Flight> flightOptional = flightRepository.findById(id);

        if (flightOptional.isPresent()) {
            Flight existingFlight = flightOptional.get();

            // Actualiza los datos del usuario
            existingFlight.setImage(flightUpdated.getImage());
            existingFlight.setCostByPerson(flightUpdated.getCostByPerson());
            existingFlight.setCostByPersonOffer(flightUpdated.getCostByPersonOffer());
            existingFlight.setAvailableSeats(flightUpdated.getAvailableSeats());
            existingFlight.setState(flightUpdated.getState());

            // Guarda el usuario actualizado en la base de datos
            Flight updatedFlightInDB = flightRepository.save(existingFlight);

            return ResponseEntity.ok(updatedFlightInDB);
        } else {
            // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

/*
    //Only use this endpoint once!
    private Map<String, String> cityToCountryMap;
    private Random random;
    int count = 1;
    @GetMapping("/populate-the-database")
    public ResponseEntity<?> populateDatabaseWithFlightsAndSeats(){
        try{
            cityToCountryMap = new HashMap<>();
            cityToCountryMap.put("Madrid", "Spain");
            cityToCountryMap.put("Londres", "United Kingdom");
            cityToCountryMap.put("New York", "United States");
            cityToCountryMap.put("Buenos Aires", "Argentina");
            cityToCountryMap.put("Miami", "United States");
            cityToCountryMap.put("Pereira", "Colombia");
            cityToCountryMap.put("Bogotá", "Colombia");
            cityToCountryMap.put("Medellín", "Colombia");
            cityToCountryMap.put("Cali", "Colombia");
            cityToCountryMap.put("Cartagena", "Colombia");
            random = new Random();
            int assignedSeats;
            int firstClassSeatsQuantity;
            int economicClassSeatsQuantity;
            int firstClassSeatRows;
            int economicClassSeatRows;

            List<String> cities = new ArrayList<>(cityToCountryMap.keySet());
            for (String origin : cities){
                for (String destination : cities){
                    if (!origin.equals(destination)){
                        String country1 = cityToCountryMap.get(origin);
                        String country2 = cityToCountryMap.get(destination);
                        boolean isInternational = !country1.equals(country2);

                        if (isInternational) {
                            //International
                            assignedSeats = 250;
                            firstClassSeatsQuantity = 50;
                            firstClassSeatRows = 9; //round up firstClassSeatsQuantity/6
                            economicClassSeatsQuantity = 200;
                            economicClassSeatRows = 34; //round up economicClassSeatsQuantity/6
                        } else {
                            //National
                            assignedSeats = 150;
                            firstClassSeatsQuantity = 25;
                            firstClassSeatRows = 5; //round up firstClassSeatsQuantity/6
                            economicClassSeatsQuantity = 125;
                            economicClassSeatRows = 21; //round up economicClassSeatsQuantity/6
                        }
                        //flight date
                        int year = 2024;
                        int dayOfYear = random.nextInt(366);
                        long millisInDay = 24 * 60 * 60 * 1000;
                        long flightDateMillis = new Date(year - 1900, 0, 1).getTime() + dayOfYear * millisInDay;
                        //arrival date
                        int maxHours = 24;
                        int hoursToAdd = random.nextInt(maxHours);
                        long arrivalDateMillis = flightDateMillis + hoursToAdd * 60 * 60 * 1000;
                        //duration
                        long durationMillis = arrivalDateMillis - flightDateMillis;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Assuming you want the duration in UTC time.

                        var flight = Flight.builder()
                                .flightDate(new Date(flightDateMillis))
                                .origin(origin)
                                .destination(destination)
                                .flightDuration(dateFormat.format(new Date(durationMillis)))
                                .arrivalDate(new Date(arrivalDateMillis))
                                .costByPerson(Math.round((100 + random.nextDouble() * 400)*100.0)/100.0)
                                .isInternational(isInternational)
                                .state(FlightState.ON_TIME.toString())
                                .availableSeats(assignedSeats)
                                .offerID(0L)
                                .build();

                        System.out.println(count + ". Origin: "+ origin + " Destination: " + destination +
                                " isInternational: " + isInternational + " assignedSeats: " + assignedSeats +
                                "flightDate: " + flight.getFlightDate() + " arrivalDate: " + flight.getArrivalDate()
                                + "duration: " + flight.getFlightDuration() + "cost: " + flight.getCostByPerson());
                        flightRepository.save(flight);
                        //now let's create the seats for this flight
                        seatCreatorService.create_seats(
                                //assignedSeats,
                                firstClassSeatsQuantity,
                                firstClassSeatRows,
                                economicClassSeatsQuantity,
                                economicClassSeatRows,
                                flight);
                    count++;
                    }
                }
            }


            return ResponseEntity.ok("Database populated succesfully");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Error populating database");
        }
    }
    */

}
