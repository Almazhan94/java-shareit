package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Добавляется пользователь: {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<UserDto> allUsers = userService.findAllUser();
        log.info("Количество пользователей в текущий момент: {}", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable int userId) {
        log.info("Ищется пользователь с идентификатором: {}", userId);
        return userService.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable int userId) {
        log.info("Удаляется пользователь с идентификатором: {}", userId);
        return userService.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable int userId, @RequestBody UserDto userDto) {
        log.info("Обновляется пользователь: {}", userDto);
        return userService.patchUser(userId, userDto);
    }
}
