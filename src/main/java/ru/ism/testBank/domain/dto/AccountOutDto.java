package ru.ism.testBank.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountOutDto {
    private UserDto user;
    private Long balance;
}
