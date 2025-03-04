package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import static ru.yandex.practicum.filmorate.utils.ValidationController.validateFilm;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {

    public static int filmId = 0;
    public final Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        log.trace("Получены все пользователи");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ParseException {
        if (validateFilm(film)) {
            film.setId(getNextId());
            films.put(film.getId(),film);
            log.info("Получены следующие значения:{}, {}, {}, {}", "film.setName()", "film.setDuration()",
                    "film.setDescription()", "film.setReleaseDate()");
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "film.setName()", "film.setDuration()",
                    "film.setDescription()", "film.setReleaseDate()");
            throw new ValidationException("Ошибка валидации фильма. Исправьте ошибку и попробуйте снова");
        }
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) throws ParseException {
        if (newFilm.getId() == null) {
            throw new ValidationException("Необходимо указать Ид фильма");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (validateFilm(newFilm)) {
                oldFilm.setName(newFilm.getName());
                oldFilm.setDuration(newFilm.getDuration());
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            log.info("Получены следующие значения:{}, {}, {}, {}", "newFilm.setName()", "newFilm.setDuration()",
                    "newFilm.setDescription()", "newFilm.setReleaseDate()");
            return oldFilm;
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "newFilm.setName()", "newFilm.setDuration()",
                    "newFilm.setDescription()", "newFilm.setReleaseDate()");
            throw new ValidationException("Ошибка валидации. Все поля на обновления должны быть заполнены");
        }
    }

    private int getNextId() {
        return ++filmId;
    }

}
