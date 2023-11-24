package com.laboratory.airlinebackend.controller.service;

import com.laboratory.airlinebackend.controller.DTO.SeatState;
import com.laboratory.airlinebackend.model.Flight;
import com.laboratory.airlinebackend.model.Seat;
import com.laboratory.airlinebackend.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeatCreatorService {


    @Autowired
    SeatRepository seatRepository;
    public void create_seats(
            //int assignedSeats,
            int firstClassSeatsQuantity,
            int firstClassSeatRows,
            int economicClassSeatsQuantity,
            int economicClassSeatRows,
            Flight flight

    ) {

        try {
            //now let's create the seats for this flight
            char letterArray[] = {'A', 'B', 'C', 'D', 'E', 'F'};
            int seatCounter = 0;
            boolean tempExtraSpace;

            //for the first class seats
            for (int l = 1; l <= firstClassSeatRows; l++) {
                //System.out.print(l + "\t");
                for (int c = 0; c < 6; c++) {
                    if (seatCounter < firstClassSeatsQuantity) {
                        if (c == 0 || c == 5) {
                            tempExtraSpace = true;
                        } else {
                            tempExtraSpace = false;
                        }
                        var seat = Seat.builder()
                                .flightId((int) flight.getId())
                                .number(l)
                                .letter(letterArray[c])
                                .state(SeatState.AVAILABLE.toString())
                                .hasExtraSpace(tempExtraSpace)
                                .seatClass("First Class")
                                .build();
                        seatRepository.save(seat);
                        seatCounter++;
                        //System.out.print(letterArray[c] + " ");
                    }
                }
                //System.out.println();
            }
            seatCounter = 0;
            //System.out.print("------------------------------------\n");
            //for the economic class seats

            for (int l = 1; l <= economicClassSeatRows; l++) {
                //System.out.print(l + "\t");
                for (int c = 0; c < 6; c++) {
                    if (seatCounter < economicClassSeatsQuantity) {
                        if (c == 0 || c == 5) {
                            tempExtraSpace = true;
                        } else {
                            tempExtraSpace = false;
                        }
                        var seat = Seat.builder()
                                .flightId((int) flight.getId())
                                .number(l)
                                .letter(letterArray[c])
                                .state(SeatState.AVAILABLE.toString())
                                .hasExtraSpace(tempExtraSpace)
                                .seatClass("Economic Class")
                                .build();
                        seatRepository.save(seat);
                        seatCounter++;
                        //System.out.print(letterArray[c] + " ");
                    }
                }
                //System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
