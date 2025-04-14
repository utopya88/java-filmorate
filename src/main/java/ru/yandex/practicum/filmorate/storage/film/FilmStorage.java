package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmResponse;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    FilmResponse findById(Long id);

    FilmResponse create(FilmDTO film);

    FilmResponse update(FilmDTO newFilm);
}