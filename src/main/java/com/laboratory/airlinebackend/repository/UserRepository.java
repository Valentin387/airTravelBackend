package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM tblUser u WHERE u.firstName = :firstName")
    User findByFirstName(String firstName);

    @Query("SELECT u FROM tblUser u WHERE u.email = :email and u.password = :password")
    User findByEmailAndPassword(String email, String password);

    @Query("SELECT u FROM tblUser u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByRole(int i);

    @Query("SELECT u FROM tblUser u WHERE u.DNI = :DNI")
    Optional<User> findByDNI(String DNI);

    @Query("SELECT u FROM tblUser u WHERE u.ID = :userId")
    User getUserById(long userId);
}
