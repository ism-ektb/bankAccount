package ru.ism.testBank.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ism.testBank.domain.model.Account;
import ru.ism.testBank.domain.model.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository <Account, Long> {
    Optional<Account> findAccountByUserUsername(String recipientName);
    Optional<Account> findAccountByUserId(Long userId);

    @Query(value = "CALL transfer_money(:id_from, :id_to, :amount_transfer);", nativeQuery = true)
    @Modifying
    @Transactional
    void transferMoney(@Param("id_from") Long id_from, @Param("id_to") Long id_to,
                          @Param("amount_transfer") Long amount_transfer);

    @Query(value = ("CALL add_percent()"), nativeQuery = true)
    @Modifying
    @Transactional
    void addPercent();
}
