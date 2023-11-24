package com.laboratory.airlinebackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tblUser")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;
    private String DNI;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String birthPlace;
    private String billingAddress;
    private String gender;
    private int role;
    private String username;
    private String profileImage;
    private Boolean active;
    private Boolean subscribedToFeed;
    private long shoppingCartID;


    public void editAttributes() {
        // Implement method to edit user attributes
    }

    public void createAdministrator() {
        // Implement method to create an admin user
    }

    public void recoverPassword() {
        // Implement password recovery logic here
    }


    public void deleteAccount() {
        // Implement method to delete user account
    }

    public void checkIn() {
        // Implement check-in logic here
    }

    public void subscribeToFeed() {
        // Implement method to subscribe to news feed
    }

    public int getRole(){
        return role;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getRealUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword01(String password) {
        this.password = password;
    }

    public void setRealUsername(String username) {
        this.username = username;
    }
}
