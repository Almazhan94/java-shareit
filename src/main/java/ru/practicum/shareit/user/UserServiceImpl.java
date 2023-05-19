package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        return userRepository.create(user);
    }

    @Override
    public User findUserById(int userId) {
        return userRepository.findUserById(userId);
    }

    @Override
    public User deleteUser(int userId) {
        return userRepository.delete(userId);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.update(user);
    }

    @Override
    public User patchUser(int userId, User user) {
        User patchUser = findUserById(userId);
        if (patchUser != null) {
            if (user.getName() != null) {
                patchUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                for (User someUser : userRepository.findAll()) {
                    if (someUser.getEmail().equals(user.getEmail()) && someUser.getId() != userId) {
                        throw new RuntimeException(String.format("Пользователь с почтовым ящиком %s уже зарегистрирован.", user.getEmail()));
                    }
                }
                patchUser.setEmail(user.getEmail());
            }
            validator(patchUser);
        } else {
            throw new RuntimeException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        return patchUser;
    }

    private void validator(@Valid User patchUser) {
        userRepository.update(patchUser);
    }

}


