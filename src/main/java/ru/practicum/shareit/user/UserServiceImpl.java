package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepositoryDb userRepositoryDb;

    @Autowired
    public UserServiceImpl(UserRepositoryDb userRepositoryDb) {
        this.userRepositoryDb = userRepositoryDb;
    }

    @Override
    public List<UserDto> findAllUser() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userRepositoryDb.findAll()) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        return userDtoList;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User userFromDto = UserMapper.toUser(userDto);
        User user = userRepositoryDb.save(userFromDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserById(int userId) {
        Optional<User> user = userRepositoryDb.findById(userId);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
    }

    @Override
    public void deleteUser(int userId) {
        userRepositoryDb.deleteById(userId);
    }

    @Override
    public UserDto patchUser(int userId, UserDto userDto) {
        UserDto findUser = findUserById(userId);
        User patchUser = UserMapper.toUser(findUser);
        if (userDto.getName() != null) {
            patchUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            patchUser.setEmail(userDto.getEmail());
        }
        userRepositoryDb.save(patchUser);
        return UserMapper.toUserDto(patchUser);
    }

    @Override
    public User findUserFromDb(int userId) {
        return userRepositoryDb.getReferenceById(userId);
    }

}


