package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUser();

    UserDto createUser(UserDto userDto);

    UserDto findUserById(int userId);

    void deleteUser(int userId);

    UserDto patchUser(int userId, UserDto userDto);

    User findUserFromDb(int userId);
}
