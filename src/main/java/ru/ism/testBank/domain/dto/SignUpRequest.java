package ru.ism.testBank.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ism.testBank.annotation.oneNotNull.OneNotNull;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OneNotNull(
        fields = {"phone", "email"},
        message = "Телефон или почта должны быть введены")
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

    @Schema(description = "Имя", example = "Jon")
    @Size(min = 1, max = 50, message = "Имя пользователя должно содержать от 1 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String firstname;

    @Schema(description = "Фамилия", example = "Петров")
    @Size(min = 1, max = 50, message = "Имя пользователя должно содержать от 1 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String lastname;

    @NotNull(message = "Дата рождения должна быть введена")
    @Past(message = "Дата рождения должна быть в прошлом")
    @Schema(description = "Дата рождения", example = "2024-03-01")
    private LocalDate birthday;


    @Schema(description = "Логин", example = "Jon")
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Телефон", example = "+79500000000")
    @Pattern(regexp = "\\+7[0-9]{10}", message = "Телефонный номер должен начинаться с +7, затем - 10 цифр")
    private String phone;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 4, max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;

    @Schema(description = "Начальный баланс", example = "100")
    @Positive(message = "сумма на счете должна быть положительной")
    @NotNull(message = "Отсутствует балланс счета")
    private Long balance;
}