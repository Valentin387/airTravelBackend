package com.laboratory.airlinebackend.controller;
import com.laboratory.airlinebackend.controller.DTO.*;
import com.laboratory.airlinebackend.controller.DTO.NewCard;
import com.laboratory.airlinebackend.model.Card;
import com.laboratory.airlinebackend.model.CardUser;
import com.laboratory.airlinebackend.model.User;
import com.laboratory.airlinebackend.repository.CardRepository;
import com.laboratory.airlinebackend.repository.CardUserRepository;
import com.laboratory.airlinebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@RestController
@RequestMapping("/api/financial-module")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@Transactional
public class FinancialModuleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardUserRepository cardUserRepository;

    @Autowired
    private CardRepository cardRepository;

    @PostMapping("/add/{id}")
    public ResponseEntity<?> addCards(@PathVariable Long id, @RequestBody NewCard newCard) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Check if the user already has the card
            Optional<Card> existingCard = cardRepository.findByNumber(newCard.getNumber());

            if (existingCard.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("La tarjeta ya está registrada.");
            }
            CardUser newCardUser;
            int quantityDebitCards = 0;
            int quantityCreditCards = 0;

            // Verificar el tipo de tarjeta y construir CardUser en consecuencia
            Card newCardRegistered;
            List<CardUser> cardUserList = cardUserRepository.findByUser(existingUser);

            //loop on the list of CardUser relationships to get the cards

            for (CardUser cardUser : cardUserList) {
                if ("debito".equalsIgnoreCase(cardUser.getCard().getType())) {
                    quantityDebitCards++;
                } else if ("credito".equalsIgnoreCase(cardUser.getCard().getType())) {
                    quantityCreditCards++;
                }
            }

            if ("debito".equalsIgnoreCase(newCard.getType())) {
                newCardRegistered = Card.builder()
                        //.id(id)
                        .balance(newCard.getBalance())
                        .type(newCard.getType())
                        .number(newCard.getNumber())
                        .name(newCard.getName())
                        .expirationDate(newCard.getExpirationDate())
                        .cvc(newCard.getCvc())
                        .build();
                newCardUser = CardUser.builder()
                        .user(existingUser)
                        .card(newCardRegistered)
                        .quantityCreditCards(quantityCreditCards)
                        .quantityDebitCards(quantityDebitCards + 1)
                        .build();
            } else if ("credito".equalsIgnoreCase(newCard.getType())) {
                newCardRegistered = Card.builder()
                        //.id(id)
                        .balance(newCard.getBalance())
                        .type(newCard.getType())
                        .number(newCard.getNumber())
                        .name(newCard.getName())
                        .expirationDate(newCard.getExpirationDate())
                        .cvc(newCard.getCvc())
                        .build();
                newCardUser = CardUser.builder()
                        .user(existingUser)
                        .card(newCardRegistered)
                        .quantityCreditCards(quantityCreditCards + 1)
                        .quantityDebitCards(quantityDebitCards)
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de tarjeta no válido");
            }

            // Guardar la instancia actualizada en la base de datos
            cardRepository.save(newCardRegistered);
            cardUserRepository.save(newCardUser);

            return ResponseEntity.ok(newCardUser);

        } else {
            // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/list/{id}")
    public ResponseEntity<?> cards(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        try{

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                List<Card> userCards = cardUserRepository.findCardsByUser(existingUser.getID());

                //System.out.println(userCards.get(0).getName());

                return ResponseEntity.ok(userCards);
            } else {
                // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
                return ResponseEntity.notFound().build();
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al obtener las tarjetas");
        }

        //return ResponseEntity.ok("Tarjetas obtenidas correctamente");
    }

    @DeleteMapping("/remove/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (cardOptional.isPresent()) {
            cardRepository.deleteById(cardId);
            cardUserRepository.deleteById(cardId);
            return ResponseEntity.ok("Tarjeta eliminada exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarjeta con ID " + cardId + " no encontrada");
        }
    }

    @PutMapping("/edit-balance/{cardId}")
    public ResponseEntity<?> updateBalance(@PathVariable Long cardId, @RequestBody UpdateBalance updatedBalance) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (cardOptional.isPresent()) {
            Card existingCard = cardOptional.get();

            // Actualiza el saldo de la tarjeta
            existingCard.setBalance(updatedBalance.getBalance());

            if (updatedBalance.getBalance() < 0) {
                return ResponseEntity.badRequest().body("El saldo actualizado debe ser mayor o igual a cero");
            }else {
                // Guarda el usuario actualizado en la base de datos
                Card updatedCardInDB = cardRepository.save(existingCard);

                return ResponseEntity.ok("saldo actualizado exitosamente");
            }
        } else {
            // Si no se encuentra la tarjeta, devuelve un error 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

}



