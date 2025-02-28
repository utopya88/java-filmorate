package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    Map<Integer, User> users = new HashMap<>();

    @GetMapping
    Collection<User> findAll() {
        log.trace("Получены все пользователи");
        return users.values();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PostMapping
    public User create(@RequestBody User user) throws ParseException {
        if (validationUser(user)) {
            user.setId(getNextId());
            users.put(user.getId(),user);
            log.info("Получены следующие значения:{}, {}, {}, {}", "user.getEmail()", "user.getName()",
                    "user.getLogin()", "user.getBirthday()");
            return user;
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "user.getEmail()", "user.getName()",
                    "user.getLogin()", "user.getBirthday()");
            throw new ValidationException("Ошибка валидации фильма. Исправьте ошибку и попробуйте снова");
        }
    }

    @PutMapping
    public User update(@RequestBody User newUser) throws ParseException {
        if (newUser.getId() == null) {
            throw new ValidationException("Необходимо указать Ид фильма");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (validationUser(newUser)) {
                oldUser.setName(newUser.getName());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setEmail(newUser.getEmail());
                oldUser.setBirthday(newUser.getBirthday());
            }
            log.info("Получены следующие значения:{}, {}, {}, {}", "newUser.getEmail()", "newUser.getName()",
                    "newUser.getLogin()", "newUser.getBirthday()");
            return oldUser;
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "newUser.getEmail()", "newUser.getName()",
                    "newUser.getLogin()", "newUser.getBirthday()");
            throw new ValidationException("Ошибка валидации. Все поля на обновления должны быть заполнены");
        }
    }

    boolean validationUser(User user) throws ParseException {
        Instant time = Instant.now();
        Instant date = user.getBirthday().toInstant();
        if (!user.getEmail().isBlank() && user.getEmail().contains("@") && !user.getLogin().isBlank() &&
                user.getLogin().contains(" ") && date.isAfter(time)) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        return true;
        } else {
            throw new ValidationException("Ошибка валидации");
        }
    }

}