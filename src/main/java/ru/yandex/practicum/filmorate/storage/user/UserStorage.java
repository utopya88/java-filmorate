package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> create(@RequestBody User user);

    Optional<User> update(@RequestBody User newUser);

    Optional<User> getUserById(Integer id);

    boolean isFindUserById(Integer id);
}
