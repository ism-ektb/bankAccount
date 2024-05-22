package ru.ism.testBank.domain.mapper;

import org.mapstruct.Mapper;
import ru.ism.testBank.domain.dto.UserDto;
import ru.ism.testBank.domain.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserListMapper {

    List<UserDto> modelsToDtos(List<User> users);
}
