package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.trace("Получены все пользователи");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ParseException {
        if (validationFilm(film)) {
            film.setId(getNextId());
            films.put(film.getId(),film);
            log.info("Получены следующие значения:{}, {}, {}, {}", "film.setName()", "film.setDuration()",
                    "film.setDescription()", "film.setReleaseDate()");
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
            if(validationFilm(newFilm)){
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

    boolean validationFilm(Film film) throws ParseException {
        Instant now = Instant.ofEpochSecond(0);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOrig = sdf.parse(String.valueOf(film.getReleaseDate()));
        Date dtRealese = sdf.parse("1985-12-28");
        if (film.getName() != null && !film.getName().isEmpty() && film.getDescription().length() <= 200
                && film.getDuration().isAfter(now) && dateOrig.after(dtRealese)) {
            return true;
        } else {
            return false;
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}