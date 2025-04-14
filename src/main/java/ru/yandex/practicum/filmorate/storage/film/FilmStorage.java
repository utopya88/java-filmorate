package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Buffer;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmResponse;
import ru.yandex.practicum.filmorate.model.dto.Film.FilmDto;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    FilmResponse findById(Long id);

    FilmResponse create(@Valid FilmDto film);

    FilmResponse update(Buffer newFilm);
}