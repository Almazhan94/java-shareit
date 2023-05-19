package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    List<User> findAllUser();

    User createUser(User user);

    User findUserById(int userId);

    User deleteUser(int userId);

    User updateUser(User user);

    User patchUser(int userId, User user);
}
