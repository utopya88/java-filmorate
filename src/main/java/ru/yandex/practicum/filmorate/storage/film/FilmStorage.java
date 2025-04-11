package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Optional;

public interface FilmStorage {

    ArrayList<Film> findAll();

    Optional<Film> create(@RequestBody Film film);

    Optional<Film> update(@RequestBody Film newFilm);

    Film findFilmById(Integer id);

    boolean isFindFilmById(Integer id);

}
