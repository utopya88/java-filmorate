package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.FilmResponse;

import java.util.LinkedHashSet;

public interface FilmService {
    FilmResponse addLike(Long idUser, Long idFilm);

    FilmResponse delLike(Long idUser, Long idFilm);

    LinkedHashSet<FilmResponse> viewRating(Long count);
}