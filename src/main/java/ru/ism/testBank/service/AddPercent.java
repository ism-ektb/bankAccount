package ru.ism.testBank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ism.testBank.domain.model.Account;
import ru.ism.testBank.repository.AccountRepository;

import java.util.List;

/**
 * Класс ответственный за логику начисления процентов
 */
@Slf4j
public class AddPercent implements Runnable {

    private AccountRepository accountRepository;

    public AddPercent(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable
    @Override
    public void run(){
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts){
            if (account.getCount() < 15){
                account.setBalance((long) (account.getBalance() * 1.05));
                account.setCount(account.getCount() + 1);
                accountRepository.save(account);
                log.info("Начислены проценты {}", account.getUser().getUsername());
            }
        }
    }
}
