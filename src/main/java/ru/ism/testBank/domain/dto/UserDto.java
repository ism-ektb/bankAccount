package ru.ism.testBank.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ c токеном доступа")
public class UserDto {

    @Schema(description = "Имя", example = "Вова")
    private String firstname;
    @Schema(description = "Фамилия", example = "Вовин")
    private String lastname;
    @Schema(description = "Логин", example = "Вова")
    private String username;
    @Schema(description = "Дата рождения", example = "2024-03-01")
    private String birthday;
    @Schema(description = "Email", example = "w@w")
    private String email;
    @Schema(description = "Телефон", example = "2024-03-01")
    private String phone;

}