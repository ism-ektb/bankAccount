package ru.ism.testBank.domain.mapper;

import org.mapstruct.Mapper;
import ru.ism.testBank.domain.dto.AccountOutDto;
import ru.ism.testBank.domain.model.Account;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface AccountMapper {

    AccountOutDto modelToDto(Account account);
}
