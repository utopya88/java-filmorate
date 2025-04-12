package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findFilmById(long id);

    Film update(Film film);

    Collection<Film> findAll();

    void putLike(long filmId, long userId);

    boolean deleteUsersLike(Film film, User user);

    Collection<Film> getPopular(long count, Optional<Integer> genreId, Optional<Integer> year);

    void deleteById(long filmId);

    List<Film> getCommonFilmsByRating(long userId, long friendId);

    Collection<Film> getFilmRecommendation(long userWantsRecomId, long userWithCommonLikesId);

    Collection<Film> getSearchResults(String query, List<String> by);

    List<Film> getSortedDirectorsFilmsByYears(long id);

    List<Film> getSortedDirectorsFilmsByLikes(long id);
}
