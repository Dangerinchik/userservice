package com.userservice.repository;

import com.userservice.entity.CardInfo;
import com.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> createUser(User user);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> getUserById(@Param("id") Long id);

    @Query(value = "SELECT * FROM users", countQuery = "SELECT COUNT(*) FROM users", nativeQuery = true)
    Page<User> getAllUsers(Pageable pageable);

    Optional<User> getUserByEmail(String email);

    @Modifying
    @Query(value = "UPDATE users SET" +
            " name = :#{#user.name}," +
            " surname = :#{#user.surname}," +
            " birth_date = :#{#user.birthDate}," +
            " email = :#{#user.email}" +
            "" +
            " WHERE id = :id", nativeQuery = true)
    void updateUserById(@Param("id") Long id, @Param("user") User user);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteUserById(@Param("id") Long id);

}