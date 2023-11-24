package com.laboratory.airlinebackend.controller;

import com.laboratory.airlinebackend.controller.service.EmailSenderService;
import com.laboratory.airlinebackend.model.ShoppingCart;
import com.laboratory.airlinebackend.repository.ShoppingCartRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.laboratory.airlinebackend.controller.DTO.RegisterRequestAdmin;
import com.laboratory.airlinebackend.controller.exceptions.EmailAlreadyTakenException;
import com.laboratory.airlinebackend.model.User;
import com.laboratory.airlinebackend.repository.UserRepository;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/admin")
@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/listAdmins")
    public ResponseEntity<?> getAllAdministrators() {
        List<User> admins = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == 2)
                .collect(Collectors.toList());

        return ResponseEntity.ok(admins);
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<String> handleEmailAlreadyTakenException(EmailAlreadyTakenException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // Use an appropriate HTTP status code
                .body("Email address is already taken."); // Provide an error message
    }

    @PostMapping("/newAdmin")
    public ResponseEntity<?> createNewAdmin(
            @RequestBody RegisterRequestAdmin requestNewAdmin
    ){
        String email = requestNewAdmin.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyTakenException("Email already exists");
        }

        requestNewAdmin.generateRandomPassword();

        var password = requestNewAdmin.getPassword();
        var firstName = requestNewAdmin.getFirstName();

        String body = "Estimado/a" + firstName +",\n" +
                "\n" +
                "Espero que este mensaje le encuentre bien.\n" +
                "\n" +
                "Hemos recibido una solicitud para autorizarlo como administrador y se la ha asignado la contraseña de su cuenta en nuestro sistema. Para proceder con la creación de una nueva contraseña temporal y garantizar el acceso adecuado a su cuenta como administrador.\n" +
                "\n" +
                "La contraseña temporal es la siguiente:\n" + password + "\n" +
                "\n" +
                "Una vez que haya accedido al enlace, siga las instrucciones proporcionadas para establecer una nueva contraseña segura. Asegúrese de elegir una contraseña que sea única y que cumpla con nuestras políticas de seguridad.\n" +
                "\n" +
                "Si el destinatario no es usted o no solicitó esta autorización, por favor póngase en contacto con nuestro equipo de soporte inmediatamente para investigar cualquier actividad no autorizada en su cuenta.\n" +
                "\n" +
                "Le agradecemos por confiar en nuestros servicios y estamos aquí para ayudarle en caso de cualquier duda o inquietud.\n" +
                "\n" +
                "Atentamente,\n" +
                "El equipo de AirTravelLabSoft";

        emailSenderService.sendEmail(email, "Contraseña temporal en AirTravelLabSoft", body);

        //just in case the root wants to do some crazy things later ... like managing what permissions get associated to what roles
        var shoppingCart = ShoppingCart.builder()
                .quantity(0)
                .totalAmount(0.0)
                .build();
        shoppingCartRepository.save(shoppingCart);
        Long shoppingCartID = shoppingCart.getID();

        var user = User.builder()
                .firstName(requestNewAdmin.getFirstName())
                .email(requestNewAdmin.getEmail())
                .role(2)
                .active(Boolean.TRUE)
                .password(passwordEncoder.encode(requestNewAdmin.getPassword()))
                .shoppingCartID(shoppingCartID)
                .build();

        var savedUser = userRepository.save(user);
/*        return AuthenticationResponse.builder()
                .build();*/
        return ResponseEntity.ok("Administrador creado exitosamente");
    }

    @DeleteMapping("/deleteAdmin")
    public ResponseEntity<?> deleteAdmin(@RequestParam("id") Long adminId) {
        if (userRepository.existsById(adminId)) {
            userRepository.deleteById(adminId);
            return ResponseEntity.ok("Administrador eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin with ID " + adminId + " not found");
        }
    }

}
