package ru.ism.testBank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ism.testBank.domain.dto.UserPatchDto;
import ru.ism.testBank.domain.model.Account;
import ru.ism.testBank.domain.model.User;
import ru.ism.testBank.exception.exception.BaseRelationshipException;
import ru.ism.testBank.exception.exception.NoFoundObjectException;
import ru.ism.testBank.repository.AccountRepository;
import ru.ism.testBank.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final AccountRepository accountRepository;


    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    public User save(User user) {
        return repository.save(user);
    }


    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    public User create(User user, Long balance) {
        if (repository.existsByUsername(user.getUsername())) {
            // Заменить на свои исключения
            throw new BaseRelationshipException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new BaseRelationshipException("Пользователь с таким email уже существует");
        }

        User saveUser = save(user);
        Account account = Account.builder().user(saveUser)
                .balance(balance).build();
        accountRepository.save(account);

        return saveUser;
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }


    /**
     * Изменение данных от текущем пользователе
     *
     * @param userPatchDto
     * @return DTO текущего пользователя
     */
    public User patchUserInfo(UserPatchDto userPatchDto) {
        User user = getCurrentUser();
        if (userPatchDto.getEmail() != null) user.setEmail(userPatchDto.getEmail());
        if (userPatchDto.getPhone() != null) user.setPhone(userPatchDto.getPhone());
        return repository.save(user);
    }

    /**
     * Удаление телефона текущего пользователя
     *
     * @return DTO текущего пользователя
     */
    public User deletePhone() {
        User user = getCurrentUser();
        if (user.getEmail() == null) throw new BaseRelationshipException("почта отсутствет, телефон удалить нельзя");
        user.setPhone(null);
        return repository.save(user);
    }

    /**
     * Удаление почты текущего пользователя
     *
     * @return DTO текущего пользователя
     */
    public User deleteEmail() {
        User user = getCurrentUser();
        if (user.getPhone() == null) throw new BaseRelationshipException("телефон отсутствет, почту удалить нельзя");
        user.setEmail(null);
        return repository.save(user);
    }

    /**
     * Метод поиска информации о пользователе
     * @param birthday в выборке будут учавствовать пользователи с датой рождения больше указанной
     * @param phone В выборке будут учавствовать пользоватери с указанным номером
     * @param firstname В выборке будут учавствовать пользователи имя которых содержит указанный текст
     * @param lastname В выборке будут учавствовать пользователи фамилия которых содержит указанный текст
     * @param email
     * @param pageRequest
     * @return возвращается список пользователей
     */
    public List<User> getAllUsers(LocalDate birthday, String phone, String firstname,
                                  String lastname, String email, PageRequest pageRequest) {
        return repository.findByManyParam(lastname, firstname, email, birthday, phone, pageRequest);
    }

    /**
     * Метод перевода денежных средств
     * @param user Инициатор перево
     * @param recipientLogin Логин получателя перерода
     * @param sum сумма перевода
     * @return Баланс счета инициатора перевода
     */
     @Transactional(isolation = Isolation.REPEATABLE_READ)
     @Retryable
     public Long transfer(User user, String recipientLogin, Long sum) {

        Account recipientAccount = accountRepository.findAccountByUserUsername(recipientLogin)
                .orElseThrow(() -> new NoFoundObjectException("Не найден счет у получателя средств"));
        Account userAccount = accountRepository.findAccountByUserId(user.getId()).get();
        if (userAccount.getBalance() < sum) new BaseRelationshipException("На счете недостаточно средств");
        userAccount.setBalance(userAccount.getBalance() - sum);
        accountRepository.save(userAccount);
        recipientAccount.setBalance(recipientAccount.getBalance() + sum);
        accountRepository.save(recipientAccount);
        return userAccount.getBalance();
    }

    /**
     * получение счета пользователя из контекста
     * @return
     */
    public Account getAccountFromContext(){
        User user = getCurrentUser();
        return accountRepository.findAccountByUserId(user.getId())
                .orElseThrow(()->new NoFoundObjectException("Ошибка в получении счета из коетекста"));
    }

    /**
     * начисление процентов на по каждому счету
     * параметр fixedDelay изменяет период начисления процентов
     */
    @Scheduled(fixedDelay = 10000)
    @Retryable
    public void addPercent(){

        AddPercent addPercent = new AddPercent(accountRepository);
        Thread thread = new Thread(addPercent);
        thread.start();

    }
}
