package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.UserNotFoundException;

import java.util.Collection;
import java.util.HashMap;

@Component
public class UserRepositoryImpl implements UserRepository {

     HashMap<Integer, User> users = new HashMap<>();

    private int id = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            throw new RuntimeException(String.format("Пользователь с идентификатором %d уже зарегистрирован.", user.getId()));
        }
        for (User someUser : users.values()) {
            if (someUser.getEmail().equals(user.getEmail())) {
                throw new RuntimeException(String.format("Пользователь с почтовым ящиком %s уже зарегистрирован.", user.getEmail()));
            }
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
    }

    @Override
    public User delete(int userId) {
        User deleteUser = users.remove(userId);
        if (deleteUser == null) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        } else {
            return deleteUser;
        }
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        return user;
    }

}
