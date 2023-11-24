package com.laboratory.airlinebackend.controller;

import com.laboratory.airlinebackend.controller.DTO.RegisterRequestOffer;
import com.laboratory.airlinebackend.model.Offer;
import com.laboratory.airlinebackend.repository.FlightRepository;
import com.laboratory.airlinebackend.repository.OfferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/offer")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class OfferController {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createNewOffer(
            @RequestBody RegisterRequestOffer requestNewOffer
    ){
        var currentDate = new java.util.Date();
        if (requestNewOffer.getValidDateRange().before(currentDate)) {
            return ResponseEntity.badRequest().body("Offer date must be after current date");
        }
        try{
            //System.out.println(requestNewOffer);
            var offer = Offer.builder()
                    .discount(requestNewOffer.getDiscount())
                    .description(requestNewOffer.getDescription())
                    .origin(requestNewOffer.getOrigin())
                    .destination(requestNewOffer.getDestination())
                    .validDateRange(requestNewOffer.getValidDateRange())
                    .build();
            offerRepository.save(offer);

            //I need to modify the attribute ´costByPersonOffer´ in every Flight that
            // matches the origin, destination and whose flightDate is lower than the validDateRange
            long currentOrderID = offer.getID();
            flightRepository.updateCostByPersonOffer(requestNewOffer.getDiscount()/100, requestNewOffer.getOrigin(), requestNewOffer.getDestination(), requestNewOffer.getValidDateRange(),currentOrderID);


            return ResponseEntity.ok("Offer created successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error creating offer " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getOffers(){
        try{
            var offers = offerRepository.findAll();
            return ResponseEntity.ok(offers);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error getting offers " + e.getMessage());
        }
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long id){
        try{
            Offer offer = offerRepository.findById(id).orElseThrow(() -> new Exception("Offer not found"));
            offerRepository.deleteById(id);

            //I need to modify the attribute ´costByPersonOffer´ in every Flight that
            // matches the origin, destination and whose flightDate is lower than the validDateRange
            flightRepository.resetCostByPersonOffer(offer.getOrigin(), offer.getDestination(), offer.getValidDateRange());

            return ResponseEntity.ok("Offer deleted successfully");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error deleting offer " + e.getMessage());
        }
    }
}
