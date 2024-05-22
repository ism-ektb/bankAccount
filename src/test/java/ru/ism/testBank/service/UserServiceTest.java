package ru.ism.testBank.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ism.testBank.domain.model.Account;
import ru.ism.testBank.domain.model.User;
import ru.ism.testBank.repository.AccountRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Slf4j

class UserServiceTest {

    @Autowired
    private  UserService service;

    @Autowired
    private AccountRepository repository;

    /**
     * Тест демонтстрирует потокобезопасность перевода. Не все переводы при высокой нагрузке
     * проходят, но деньги не теряются и не начисляются лишние средства
     *
     * для работоспособности теста в базе должет быть клиент с id 5 (отправитель) и
     * клиент с логином "Jonson2".
     */
    @Test
    @SneakyThrows
    public void transfer() {

        Account account1 = repository.findAccountByUserId(5L).orElseThrow(() -> new RuntimeException());
        Account account2 = repository.findAccountByUserUsername("Jonson2").orElseThrow(() -> new RuntimeException());

        long startBalance = account1.getBalance() + account2.getBalance();

        for (int i = 0; i < 10; i++) {
            Transfer1 transfer1 = new Transfer1(service);
            Thread thread = new Thread(transfer1);
            thread.start();
        }
        //ждем пока все потоки выполнятся
        long start = System.currentTimeMillis();
        long end = start + 10 * 1000;
        while (System.currentTimeMillis() < end) {
        }

        //проверяем, не потерялись ли деньги из-за отмены транзакций

        Account account12 = repository.findAccountByUserId(5L).orElseThrow(() -> new RuntimeException());
        Account account22 = repository.findAccountByUserUsername("Jonson2").orElseThrow(() -> new RuntimeException());

        long finishBalance = account12.getBalance() + account22.getBalance();

        assertEquals(startBalance, finishBalance);


    }

}

@Slf4j

class Transfer1 implements Runnable {


    private UserService service;

    public Transfer1(UserService service) {
        this.service = service;
    }


    @Override
    public void run() {
        Long balance = service.transfer(User.builder().id(5L).build(), "Jonson2", 15L);
        log.info("Остаток на счете {}", balance);
    }



}

