package ru.ism.testBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ism.testBank.domain.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository <Account, Long> {
    Optional<Account> findAccountByUserUsername(String recipientName);
    Optional<Account> findAccountByUserId(Long userId);

}
