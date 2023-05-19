package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User create(User user);

    User findUserById(int userId);

    User delete(int userId);

    User update(User user);

}
