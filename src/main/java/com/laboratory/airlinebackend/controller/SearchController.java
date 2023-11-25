package com.laboratory.airlinebackend.controller;
import com.laboratory.airlinebackend.model.Flight;
import com.laboratory.airlinebackend.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Date;


@RestController
@RequestMapping("/api/search")
@CrossOrigin("https://air-travel-frontend-5f0ebcf08ae2.herokuapp.com")
@RequiredArgsConstructor
public class SearchController {

    @Autowired
    FlightRepository flightRepository;

    @GetMapping("/parametrized-search")
    public ResponseEntity<?> searchFlights(
            @RequestParam  String origin,
            @RequestParam  String destination,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") Date flightDate,
            @RequestParam  int numPassengers) {

        try {
            List<Flight> flights = flightRepository.findFlightsByParameters(origin, destination, flightDate, numPassengers);

            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching flights " + e.getMessage() );
        }

    }

    @GetMapping("/{destinationName}")
    public ResponseEntity<List<Flight>> getOnTimeFlightsForDestination(@PathVariable String destinationName) {
        // Use the destinationName to retrieve ON_TIME flights for that destination
        try {
            List<Flight> onTimeFlights = flightRepository.getOnTimeFlightsForDestination(destinationName);

            return ResponseEntity.ok(onTimeFlights);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }


}
