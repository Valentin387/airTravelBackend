package com.laboratory.airlinebackend.config;


import com.laboratory.airlinebackend.model.Role;
import com.laboratory.airlinebackend.model.RolePermission;
import com.laboratory.airlinebackend.repository.RolePermissionRepository;
import com.laboratory.airlinebackend.repository.RoleRepository;
import com.laboratory.airlinebackend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class JwtService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    private static final String SECRET_KEY = "320556751e41ce6030fc383bf5ec443eda02a361e450f2081dd686363936f74e";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> ClaimsResolver){
        final Claims claims = extractAllClaims(token);
        return ClaimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        // Create a new map for custom claims
        Map<String, Object> claims = new HashMap<>();

        var u1 = userRepository.findByEmail(userDetails.getUsername()).
                orElseThrow();
        // Add custom claims for role and permissions
        Role userRole = roleRepository.findById((long) u1.getRole()).orElse(null);

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(userRole);

        List<String> permissionNames = rolePermissions.stream()
                .map(rolePermission -> rolePermission.getPermission().getPermission())
                .collect(Collectors.toList());

        assert userRole != null;
        claims.put("ID", u1.getID());
        claims.put("role", userRole.getRole()); // This will include the user's roles
        claims.put("permissions", permissionNames); // Custom method to get permissions as a string or list

        return generateToken(claims, userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
