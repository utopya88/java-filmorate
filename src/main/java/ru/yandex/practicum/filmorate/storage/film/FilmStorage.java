package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.model.FilmResponse;

import java.util.*;

public interface FilmStorage {

    List<Film> findAll();

    FilmResponse findById(Long id);

    FilmResponse create(@Valid FilmDto buffer);

    FilmResponse update(FilmDto newFilm);

    Map<Long, Set<Long>> selectLikedUsers();

    void insertLike(Long idUser, Long idFilm);

    Map<Long, LinkedHashSet<Long>> selectFilmGenre(Long filmId);

    void deleteLike(Long idUser, Long idFilm);

    LinkedHashMap<Long, Long> selectTopFilms();

}