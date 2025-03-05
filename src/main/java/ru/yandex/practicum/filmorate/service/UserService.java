package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import static ru.yandex.practicum.filmorate.utils.ValidationController.validateUser;


import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    public static int userId = 0;
    public final Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
         log.trace("Получены все пользователи");
         return users.values();
    }

    public User create(@RequestBody User user) throws ParseException {
        if (validateUser(user)) {
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

    public User update(@RequestBody User newUser) throws ParseException {
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
            return oldUser;
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "newUser.getEmail()", "newUser.getName()",
                    "newUser.getLogin()", "newUser.getBirthday()");
            throw new ValidationException("Ошибка валидации. Все поля на обновления должны быть заполнены");
        }
    }

    private int getNextId() {
        return ++userId;
    }
}
