package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static ru.yandex.practicum.filmorate.utils.ValidationController.validateUser;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private static int userId = 0;

    private final Map<Integer,User> users = new HashMap<>();

    @Override
    public ArrayList<User> findAll() {
        log.trace("Получены все пользователи");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> create(@RequestBody User user) {
        if (validateUser(user)) {
            user.setId(getNextId());
            users.put(user.getId(),user);
            log.info("Получены следующие значения:{}, {}, {}, {}", "user.getEmail()", "user.getName()",
                    "user.getLogin()", "user.getBirthday()");
            return Optional.of(user);
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "user.getEmail()", "user.getName()",
                    "user.getLogin()", "user.getBirthday()");
            throw new ValidationException("Ошибка валидации фильма. Исправьте ошибку и попробуйте снова");
        }
    }

    @Override
    public Optional<User> update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Необходимо указать Ид фильма");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (validateUser(newUser)) {
                oldUser.setName(newUser.getName());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setEmail(newUser.getEmail());
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Получены следующие значения:{}, {}, {}, {}", "newUser.getEmail()", "newUser.getName()",
                    "newUser.getLogin()", "newUser.getBirthday()");
            return Optional.of(oldUser);
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "newUser.getEmail()", "newUser.getName()",
                    "newUser.getLogin()", "newUser.getBirthday()");
            throw new ValidationException("Ошибка валидации. Все поля на обновления должны быть заполнены");
        }
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        if (users.get(id) == null) {
            throw new FindObjectException("Не найден пользователь");
        }
        return Optional.of(users.get(id));
    }

    private int getNextId() {
        return ++userId;
    }

    @Override
    public boolean isFindUserById(Integer id) {
        if (users.get(id) == null) {
            throw new FindObjectException("ошибка, идентификатор равен нулю");
        }
        return true;
    }
}
