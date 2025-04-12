package ru.yandex.practicum.filmorate.validation;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@NoArgsConstructor
@Slf4j
public class ValidationController {

    public static boolean validateFilm(Film film) {
        ValidationController.validateName(film.getName());
        ValidationController.validateDescription(film.getDescription());
        ValidationController.validateReleaseDate(film.getReleaseDate());
        ValidationController.validateDuration(film.getDuration());
        log.trace("валидация фильма прошла успешно.");
        return true;
    }

    private static void validateName(String name) {
        if (name.isEmpty() || name.isBlank()) {
            throw new ValidationException("Ошибка валидации. Название не может быть пустым");
        }
    }

    private static void validateDescription(String description) {
        if (description.length() > 200) {
            throw new ValidationException("Ошибка валидации. Максимальная длина описания 200 символов");
        }
    }

    private static void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка валидации. Дата релиза — не раньше 28 декабря 1895 года.");
        }
    }

    private static void validateDuration(int duration) {
        if (duration < 0) {
            throw new ValidationException("Ошибка валидации. Продолжительность фильма должна быть положительной.");
        }
    }

    public static boolean validateUser(User user) {
        ValidationController.validateEmail(user.getEmail());
        ValidationController.validateLogin(user.getLogin());
        user = validateUserName(user);
        ValidationController.validateBirthday(user.getBirthday());
        log.trace("Валидация пользователя прошла успешно.");
        return true;
    }

    private static User validateUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    private static void validateEmail(String email) {
        if (email.isEmpty() || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Ошибка валидации. Неверный email формат.");
        }
    }

    private static void validateLogin(String login) {
        if (login.isEmpty() || login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Ошибка валидации. Логин не может быть пустым или с пробелами");
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка валидации. Вы из будущего?)");
        }
    }

}