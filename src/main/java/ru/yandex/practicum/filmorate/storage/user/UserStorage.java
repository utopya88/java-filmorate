package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Optional;

public interface UserStorage {

    ArrayList<User> findAll();

    Optional<User> create(@RequestBody User user);

    Optional<User> update(@RequestBody User newUser);

    int getNextId();

    User getUserById(Integer id);

    boolean isFindUserById(Integer id);
}
