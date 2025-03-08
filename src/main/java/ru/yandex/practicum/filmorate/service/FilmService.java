package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
        Film film = getFilm(id);
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Этот пользователь уже ставил лайк фильму");
        }
        if (!userStorage.isFindUserById(userId)) {
            throw new FindObjectException("Такого пользователя не существует");
        }
        int rate = film.getRate() + 1;
        film.setRate(rate);
        film.getLikes().add(userId);
        log.trace("Добавили лайк и пользователя который поставил лайк фильму");
        return film.getRate();
    }

    public Integer deleteLike(Integer id, Integer userId) {
        Film film = getFilm(id);
        if (!film.getLikes().contains(userId)) {
            throw new FindObjectException("Не найден лайк пользователя на фильме пользователь");
        }
        if (film.getRate() == null) {
            throw new ValidationException("Значение рейтинга равна нулю");
        }
        int rate = film.getRate() - 1;
        film.setRate(rate);
        film.getLikes().remove(userId);
        log.trace("Удалили лайк и пользователя который поставил лайк");
        return film.getRate();
    }

    public List<Film> getFilmsForLikes(Integer count) {
        if (count < 0) {
            log.warn("Фиговый каунт");
            throw new ValidationException("Ошибка. Получено значение меньше нуля");
        }
        ArrayList<Film> hash = new ArrayList<>(filmStorage.findAll());
        hash.sort(Comparator.comparing(Film::getRate));
        ArrayList<Film> finalFilms = new ArrayList<>();
        for (int i = 0; i < count; i++){
            finalFilms.add(hash.get(i));
        }
        return finalFilms;
    }

    public Film create(Film film) {
        if (filmStorage.findAll().contains(film)) {
            throw new ValidationException("Данный фильм уже существует");
        }
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

    public Film getFilm(Integer id) {
        return filmStorage.findFilmById(id).get();
    }
}
