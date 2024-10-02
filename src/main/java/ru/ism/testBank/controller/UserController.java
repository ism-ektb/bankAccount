package ru.ism.testBank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ism.testBank.domain.dto.AccountOutDto;
import ru.ism.testBank.domain.dto.UserDto;
import ru.ism.testBank.domain.dto.UserPatchDto;
import ru.ism.testBank.domain.mapper.AccountMapper;
import ru.ism.testBank.domain.mapper.UserListMapper;
import ru.ism.testBank.domain.mapper.UserMapper;
import ru.ism.testBank.domain.model.User;
import ru.ism.testBank.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "Работа с учетной записью", description = "Доступен только авторизованным пользователям")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private final UserListMapper listMapper;
    private final AccountMapper accountMapper;

    @GetMapping
    @Operation(summary = "Информация о текущем пользователе")
    public AccountOutDto getInfo() {
        return accountMapper.modelToDto(service.getAccountFromContext());
    }

    @PatchMapping
    @Operation(summary = "Изменение данных текущего пользователя")
    public UserDto patchInfo(@Valid @RequestBody UserPatchDto userPatchDto){
        return mapper.modelToDto(service.patchUserInfo(userPatchDto));
    }

    @DeleteMapping("/phone")
    @Operation(summary = "Удаление телефона текущего пользователя")
    public UserDto deletePhone(){
        return mapper.modelToDto(service.deletePhone());
    }

    @DeleteMapping("/email")
    @Operation(summary = "Удаление почты текущего пользователя")
    public UserDto deleteEmail(){
        return mapper.modelToDto(service.deleteEmail());
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск пользователей")
    public List<UserDto> search(@RequestParam(required = false, defaultValue = "1900-01-01") LocalDate birthday,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String firstname,
                                @RequestParam(required = false) String lastname,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false, defaultValue = "0")
                                    Integer from,
                                @RequestParam(required = false, defaultValue = "10")
                                    Integer size) {

        return listMapper.modelsToDtos(service.getAllUsers(birthday, phone, firstname, lastname, email, PageRequest.of(from / size, size)));
    }

    @PatchMapping("/transfer")
    @Operation(summary = "перевод денег со счета")
    public Long transfer(@RequestParam String recipient,
                         @RequestParam @Positive Long sum){
        User user = service.getCurrentUser();
        Long newBalance = service.transfer(user, recipient, sum);
        log.info("успешный перевод на суммы {} от {} к {}", sum, user.getUsername(), recipient);
        return newBalance;
    }
}