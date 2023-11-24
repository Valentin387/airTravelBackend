package com.laboratory.airlinebackend.controller;


import com.laboratory.airlinebackend.controller.DTO.UpdateProfileRequest;
import com.laboratory.airlinebackend.model.User;
import com.laboratory.airlinebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;


@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> register(@PathVariable Long id){
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            return ResponseEntity.ok(UpdateProfileRequest.builder()
                    .email(existingUser.getEmail())
                    .DNI(existingUser.getDNI())
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .birthday(existingUser.getBirthday())
                    .birthPlace(existingUser.getBirthPlace())
                    .billingAddress(existingUser.getBillingAddress())
                    .gender(existingUser.getGender())
                    .role(existingUser.getRole())
                    .username(existingUser.getRealUsername())
                    .profileImage(existingUser.getProfileImage())
                    .active(existingUser.getActive())
                    .subscribedToFeed(existingUser.getSubscribedToFeed())
                    .build());
        } else {
            // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/profileUpdate/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Actualiza los datos del usuario
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setDNI(updatedUser.getDNI());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setBirthday(updatedUser.getBirthday());
            existingUser.setBirthPlace(updatedUser.getBirthPlace());
            existingUser.setBillingAddress(updatedUser.getBillingAddress());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setRole(updatedUser.getRole());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setProfileImage(updatedUser.getProfileImage());
            existingUser.setActive(updatedUser.getActive());
            existingUser.setSubscribedToFeed(updatedUser.getSubscribedToFeed());

            // Guarda el usuario actualizado en la base de datos
            User updatedUserInDB = userRepository.save(existingUser);

            return ResponseEntity.ok(updatedUserInDB);
        } else {
            // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping ("/profileDelete/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Elimina el usuario de la base de datos
            userRepository.delete(existingUser);

            return ResponseEntity.ok("User deleted successfully");
        } else {
            // Si no se encuentra el usuario, devuelve un error 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

}

