package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OfferRepository  extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM tblOffer o WHERE o.validDateRange >= :flightDate AND o.origin = :origin AND o.destination = :destination")
    List<Offer> findValidOffers(String origin, String destination, Date flightDate);
}
