package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryDb extends JpaRepository<User, Integer> {

}
