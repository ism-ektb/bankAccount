package ru.ism.testBank.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.ism.testBank.domain.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    /*

    @Query("SELECT u FROM User AS u " +
            "WHERE ((u.birthday > ?1) OR (?1 IS NULL)) " +
            "AND ((u.phone = ?2) OR (?2 IS NULL)) " +
            "AND ((u.firstname LIKE CONCAT('%', ?3, '%')) OR (?3 IS NULL)) " +
            "AND ((u.lastname LIKE CONCAT('%', ?4, '%')) OR (?4 IS NULL))" +
            "AND ((u.email = ?5) OR (?5 IS NULL)) " +
            "ORDER BY u.firstname")
    List<User> findByManyParam(Date birthday, String phone, String firstname, String lastname, String email, Pageable pageable);


     */

    @Query("SELECT u FROM User AS u " +
            "WHERE  ((LOWER(u.lastname) LIKE LOWER(CONCAT('%', ?1, '%'))) OR (?1 IS NULL)) " +
            "AND ((LOWER(u.firstname) LIKE LOWER(CONCAT('%', ?2, '%'))) OR (?2 IS NULL)) " +
            "AND ((u.email = ?3) OR (?3 IS NULL)) " +
            "AND (u.birthday > ?4) " +
            "AND ((u.phone = ?5) OR (?5 IS NULL)) " +
            "ORDER BY u.firstname")
    List<User> findByManyParam(String lastname, String firstname, String email, LocalDate birthday,
                               String phone, Pageable pageable);


}