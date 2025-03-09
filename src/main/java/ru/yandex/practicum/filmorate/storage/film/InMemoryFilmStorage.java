package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import static ru.yandex.practicum.filmorate.utils.ValidationController.validateFilm;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    public static int filmId = 0;

    public final Map<Integer, Film> films = new HashMap<>();

    @Override
    public ArrayList<Film> findAll() {
        log.trace("Получены все пользователи");
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> create(@RequestBody Film film) {
        if (validateFilm(film)) {
            film.setId(getNextId());
            films.put(film.getId(),film);
            log.info("Получены следующие значения:{}, {}, {}, {}", "film.setName()", "film.setDuration()",
                    "film.setDescription()", "film.setReleaseDate()");
            films.put(film.getId(), film);
            return Optional.of(film);
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "film.setName()", "film.setDuration()",
                    "film.setDescription()", "film.setReleaseDate()");
            throw new ValidationException("Ошибка валидации фильма. Исправьте ошибку и попробуйте снова");
        }
    }

    @Override
    public Optional<Film> update(@RequestBody Film newFilm) {
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
            return Optional.of(oldFilm);
        } else {
            log.warn("Получены следующие значения:{}, {}, {}, {}", "newFilm.setName()", "newFilm.setDuration()",
                    "newFilm.setDescription()", "newFilm.setReleaseDate()");
            throw new ValidationException("Ошибка валидации. Все поля на обновления должны быть заполнены");
        }
    }

    public Film getFilmById(Integer id) {
        return films.get(id);
    }

    public Film findFilmById(Integer filmId) {
        if (films.get(filmId) == null) {
            log.warn("Фильм № {} не найден", filmId);
            throw new FindObjectException("Фильм не найден");
        }
        return films.get(filmId);
    }

    private int getNextId() {
        return ++filmId;
    }

    public boolean isFindFilmById(Integer filmId) {
        if (films.get(filmId) == null) {
            log.warn("Фильм № {} не найден", filmId);
            throw new FindObjectException("Идентификатор фильма равен нулю");
        }
        return true;
    }
}
