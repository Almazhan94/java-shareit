package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Количество пользователей в текущий момент: {}", userService.findAllUser().size());
        return userService.findAllUser();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {
        log.info("Ищется пользователь: {}", userService.findUserById(userId));
        return userService.findUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable int userId) {
        log.info(String.format("Удаляется пользователь с идентификатором %d ", userId));
        return userService.deleteUser(userId);
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Добавляется пользователь: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Обновляется пользователь: {}", user);
        return userService.updateUser(user);
    }

    @PatchMapping("/{userId}")
    public User patch(@PathVariable int userId, @RequestBody User user) {
        log.info("Обновляется пользователь: {}", user);
        return userService.patchUser(userId, user);
    }
}
