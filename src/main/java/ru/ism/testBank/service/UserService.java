package ru.ism.testBank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ism.testBank.domain.dto.UserPatchDto;
import ru.ism.testBank.domain.model.Account;
import ru.ism.testBank.domain.model.User;
import ru.ism.testBank.exception.exception.BaseRelationshipException;
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
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
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

    
    public List<User> getAllUsers(LocalDate birthday, String phone, String firstname,
                                  String lastname, String email, PageRequest pageRequest) {
        return repository.findByManyParam(lastname, firstname, email, birthday, phone, pageRequest);
    }

    public Long transfer(User user, String recipientLogin, Long sum) {
        User recipient = getByUsername(recipientLogin);
        accountRepository.transferMoney(user.getId(), recipient.getId(), sum);
        Account account = accountRepository.findAccountByUserId(user.getId()).orElseThrow(() -> new RuntimeException());
        return account.getBalance();
    }
}
