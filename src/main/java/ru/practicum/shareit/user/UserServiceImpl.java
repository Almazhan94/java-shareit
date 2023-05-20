package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.ArrayList;
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
    public List<UserDto> findAllUser() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user :  userRepository.findAll()) {
            userDtoList.add(UserMapper.toUserDto(user));
        }
        return userDtoList;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User userFromDto = UserMapper.toUser(userDto);
        User user = userRepository.create(userFromDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserById(int userId) {
        User user = userRepository.findUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUser(int userId) {
        User user = userRepository.delete(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto patchUser(int userId, UserDto userDto) {
        User patchUser = userRepository.findUserById(userId);
        if (patchUser != null) {
            if (userDto.getName() != null) {
                patchUser.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                for (User someUser : userRepository.findAll()) {
                    if (someUser.getEmail().equals(userDto.getEmail()) && someUser.getId() != userId) {
                        throw new RuntimeException(String.format("Пользователь с почтовым ящиком %s уже зарегистрирован.", userDto.getEmail()));
                    }
                }
                patchUser.setEmail(userDto.getEmail());
            }
            validator(patchUser);
        } else {
            throw new RuntimeException(String.format("Пользователь с идентификатором %d не существует", userId));
        }
        return UserMapper.toUserDto(patchUser);
    }

    private void validator(@Valid User patchUser) {
        userRepository.update(patchUser);
    }

}


