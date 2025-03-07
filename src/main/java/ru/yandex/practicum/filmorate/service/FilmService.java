package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Integer addLike(Integer id, Integer userId) {
        Film film = filmStorage.findFilmById(id).get();
        User user =  userStorage.getUserById(userId).get();
        if (film.getLikes().contains(user.getId())) {
            throw new ValidationException("Этот пользователь уже ставил лайк фильму");
        }
        film.setRate(film.getRate() + 1);
        film.getLikes().add(user.getId());
        log.trace("Добавили лайк и пользователя который поставил лайк фильму");
        return film.getRate();
    }

    public Integer deleteLike(Integer id, Integer userId) {
        Film film = filmStorage.findFilmById(id).get();
        User user = userStorage.getUserById(userId).get();
        if (!film.getLikes().contains(user.getId())) {
            throw new FindObjectException("Не найден лайк пользователя на фильме пользователь");
        }
        film.setRate(film.getRate() - 1);
        film.getLikes().remove(user.getId());
        log.trace("Удалили лайк и пользователя который поставил лайк");
        return film.getRate();
    }

    public List<Film> getFilmsForLikes(Integer count) {
        if (count < 0) {
            log.warn("Фиговый каунт");
            throw new ValidationException("Ошибка. Получено значение меньше нуля");
        }
        List<Film> result = filmStorage.findAll().stream().sorted(Comparator.comparing(Film::getRate)).toList();
        log.trace("Фильтранулись на кол-во лайков и вывели заданное кол-во лайков");
        return result.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film).get();
    }

    public Film update(Film film) {
        if (!filmStorage.isFindFilmById(film.getId())) {
            throw new FindObjectException("Не найден обьект для обновления");
        }
        return filmStorage.update(film).get();
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAll();
    }

}
