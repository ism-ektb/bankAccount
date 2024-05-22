package ru.ism.testBank.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ism.testBank.repository.AccountRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60);
                    accountRepository.addPercent();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }
            }
        });
        t.start();
    }

}
