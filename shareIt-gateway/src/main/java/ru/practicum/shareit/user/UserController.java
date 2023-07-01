package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.Generated;
import ru.practicum.shareit.user.dto.UserDto;

@Generated
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(CreateStep.class) UserDto userDto) {
        log.info("Добавляется пользователь: {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Ищутся все пользователи");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable int userId) {
        log.info("Ищется пользователь с идентификатором: {}", userId);
        return userClient.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable int userId) {
        log.info("Удаляется пользователь с идентификатором: {}", userId);
        userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patch(@PathVariable int userId, @RequestBody @Validated(UpdateStep.class) UserDto userDto) {
        log.info("Обновляется пользователь: {}", userDto);
        return userClient.patchUser(userId, userDto);
    }
}
