package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    User user;
    User user2;


    @BeforeEach
    void setUp() {

        userService = new UserServiceImpl(userRepository);

        user = new User(1, "name", "e@mail.com");

        user2 = new User(2, "name2", "e2@mail.com");
    }

    @Test
    void findAllUserTest() {
        when(userRepository.findAll())
                .thenReturn(List.of(user, user2));

        List<UserDto> userDtoList = userService.findAllUser();

        assertEquals(userDtoList.size(), 2);
        assertEquals(userDtoList.get(0).getId(), user.getId());
        assertEquals(userDtoList.get(1).getId(), user2.getId());
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.findUserById(user.getId());

        assertEquals(userDto.getId(), user.getId());
    }

    @Test
    void patchUserTest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.patchUser(user.getId(), new UserDto(null, "aaa", "aaa@mail.com"));

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(),"aaa");
        assertEquals(userDto.getEmail(), "aaa@mail.com");
    }
}