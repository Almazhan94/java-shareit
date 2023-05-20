package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {

    Collection<User> findAll();

    User create(User user);

    User findUserById(int userId);

    User delete(int userId);

    User update(User user);

}
