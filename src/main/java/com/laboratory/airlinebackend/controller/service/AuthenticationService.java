package com.laboratory.airlinebackend.controller.service;

import com.laboratory.airlinebackend.config.JwtService;
import com.laboratory.airlinebackend.controller.DTO.AuthenticationRequest;
import com.laboratory.airlinebackend.controller.DTO.AuthenticationResponse;
import com.laboratory.airlinebackend.controller.DTO.RegisterRequest;
import com.laboratory.airlinebackend.controller.exceptions.EmailAlreadyTakenException;
import com.laboratory.airlinebackend.model.ShoppingCart;
import com.laboratory.airlinebackend.model.User;
import com.laboratory.airlinebackend.model.token.Token;
import com.laboratory.airlinebackend.model.token.TokenRepository;
import com.laboratory.airlinebackend.model.token.TokenType;
import com.laboratory.airlinebackend.repository.ShoppingCartRepository;
import com.laboratory.airlinebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyTakenException("Email already exists");
        }
        var shoppingCart = ShoppingCart.builder()
                .quantity(0)
                .totalAmount(0.0)
                .build();
        shoppingCartRepository.save(shoppingCart);
        Long shoppingCartID = shoppingCart.getID();


        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .DNI(request.getDNI())
                .birthday(request.getBirthday())
                .birthPlace(request.getBirthPlace())
                .billingAddress(request.getBillingAddress())
                .gender(request.getGender())
                .profileImage(request.getProfileImage())
                .subscribedToFeed(Boolean.FALSE)
                .role(3)
                .shoppingCartID(shoppingCartID)
                .active(Boolean.TRUE)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user); //just in case
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getID());
        if(validUserTokens.isEmpty()) {
            return;
        }else{
            validUserTokens.forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }

    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).
                orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
