package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceImplIntegrationTest {

    @Autowired
    UserService userService;

    User user;
    User user2;


    @BeforeEach
    void setUp() {

        user = new User(1, "name", "e@mail.com");

        user2 = new User(2, "name2", "e2@mail.com");
    }


    @Test
    @DirtiesContext
    void findAllUser() {
        user.setId(null);
        user2.setId(null);
        userService.createUser(UserMapper.toUserDto(user));
        userService.createUser(UserMapper.toUserDto(user2));

        List<UserDto> userDtoList = userService.findAllUser();

        assertEquals(userDtoList.size(), 2);
        assertEquals(userDtoList.get(0).getId(), 1);
        assertEquals(userDtoList.get(1).getId(), 2);
    }

    @Test
    @DirtiesContext
    void createUser() {
        user.setId(null);
        UserDto userDtoToSave = UserMapper.toUserDto(user);

        UserDto userDto = userService.createUser(userDtoToSave);

        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    @DirtiesContext
    void findUserById() {
       userService.createUser(UserMapper.toUserDto(user));

        UserDto userDto = userService.findUserById(1);

        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());

    }

    @Test
    @DirtiesContext
    void deleteUser() {
        userService.createUser(UserMapper.toUserDto(user));

        userService.deleteUser(1);

       assertThrows(UserNotFoundException.class, () -> userService.findUserById(1));
    }

    @Test
    @DirtiesContext
    void patchUser() {
        userService.createUser(UserMapper.toUserDto(user));

        UserDto userDto = userService.patchUser(1,
                new UserDto(null, "another name", "another@mail.com"));

        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), "another name");
        assertEquals(userDto.getEmail(), "another@mail.com");
    }
}