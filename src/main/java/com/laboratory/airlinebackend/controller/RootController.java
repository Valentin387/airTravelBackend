package com.laboratory.airlinebackend.controller;

import java.util.Optional;
import com.laboratory.airlinebackend.controller.DTO.ChangeIdRootRequest;
import com.laboratory.airlinebackend.controller.exceptions.RootIdChangeException;
import com.laboratory.airlinebackend.model.User;
import com.laboratory.airlinebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/root")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class RootController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/rootChangeID")
    public ResponseEntity<?> changeIdRoot(@RequestBody ChangeIdRootRequest requestchangeIdRoot) {
        String newEmail = requestchangeIdRoot.getEmail();

        // Busca al usuario root por su rol o algún otro criterio que lo identifique como root.
        Optional<User> userRootOptional = userRepository.findByRole(1);

        if (userRootOptional.isPresent()) {
            User userRoot = userRootOptional.get();

            // Verifica si el nuevo correo electrónico ya existe en la base de datos.
            if (userRepository.existsByEmail(newEmail)) {
                throw new RootIdChangeException("Email already exists");
            }

            // Actualiza el correo electrónico del usuario root y guárdalo en la base de datos.
            userRoot.setEmail(newEmail);
            userRepository.save(userRoot);

            return ResponseEntity.ok("Root email changed successfully");
        } else {
            throw new RootIdChangeException("Root user not found");
        }
    }


}
