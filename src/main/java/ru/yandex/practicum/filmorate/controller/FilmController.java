package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.ValidatorControllers;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    @Validated
    public Film create(@Valid @RequestBody Film film) {
        film = ValidatorControllers.validateFilm(film);
        Film newFilm = filmService.create(film);
        log.debug("Добавлен новый фильм: {}", newFilm);
        return newFilm;
    }

    @PutMapping("/films")
    @Validated
    public Film update(@Valid @RequestBody Film film) {
        ValidatorControllers.validateFilm(film);
        Film newFilm = filmService.update(film);
        log.debug("Обновлен фильм: {}", newFilm);
        return newFilm;
    }

    @DeleteMapping("/films")
    @Validated
    public void delete(@Valid @RequestBody Film film) {
        filmService.delete(film);
        log.debug("Удалён фильм: {}", film);
    }

    @DeleteMapping("/films/{filmId}")
    @Validated
    public void deleteFilmById(@Valid @PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
        log.debug("Удалён фильм c id: {}", filmId);
    }

    @GetMapping("/films")
    public List<Film> findFilms() {
        List<Film> films = filmService.findFilms();
        log.debug("Получен список фильмов, количество = {}", films.size());
        return films;
    }

    @GetMapping("/films/{filmId}")
    public Film findFilmById(@PathVariable long filmId) {
        Film film = filmService.findFilmById(filmId);
        log.debug("Получен фильм с id = {}", filmId);
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public boolean like(@PathVariable long id, @PathVariable long userId,
                        @RequestParam(defaultValue = "6") Integer mark) {
        if (filmService.like(id, userId, mark)) {
            log.debug("Пользователь id = {} поставил оценку {} фильму id = {}", userId, mark, id);
            return true;
        }
        return false;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean dislike(@PathVariable long id, @PathVariable long userId) {
        if (filmService.dislike(id, userId)) {
            log.debug("Пользователь id = {} удалил оценку с фильма id = {}", userId, id);
            return true;
        }
        return false;
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count,
                                       @RequestParam(required = false) Long genreId,
                                       @RequestParam(required = false) Integer year) {
        List<Film> films = filmService.findPopularFilms(count, genreId, year);
        log.debug("Получен список из первых {} фильмов по количеству оценок, " +
                "количество = {}", count, films.size());
        return films;
    }

    @GetMapping("/genres")
    public List<Genre> findGenres() {
        List<Genre> genres = filmService.findGenres();
        log.debug("Получен список жанров, количество = {}", genres.size());
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable long id) {
        Genre genre = filmService.findGenreById(id);
        log.debug("Получен жанр с id = {}", id);
        return genre;
    }

    @GetMapping("/mpa")
    public List<RatingMPA> findRatingMPAs() {
        List<RatingMPA> ratingMPAs = filmService.findRatingMPAs();
        log.debug("Получен список рейтингов МПА, количество = {}", ratingMPAs.size());
        return ratingMPAs;
    }

    @GetMapping("/mpa/{id}")
    public RatingMPA findRatingMPAById(@PathVariable long id) {
        RatingMPA ratingMPA = filmService.findRatingMPAById(id);
        log.debug("Получен рейтинг МПА с id = {}", id);
        return ratingMPA;
    }

    @GetMapping("/directors")
    public List<Director> findDirectors() {
        List<Director> directors = filmService.findDirectors();
        log.debug("Получен список режиссёров, количество = {}", directors.size());
        return directors;
    }

    @GetMapping("/directors/{id}")
    public Director findDirectorById(@PathVariable long id) {
        Director director = filmService.findDirectorById(id);
        log.debug("Получен режиссёр с id = {}", id);
        return director;
    }

    @PostMapping("/directors")
    public Director createDirector(@RequestBody Director director) {
        director = ValidatorControllers.validateDirector(director);
        Director newDirector = filmService.createDirector(director);
        log.debug("Добавлен новый режиссёр: {}", newDirector);
        return newDirector;
    }

    @PutMapping("/directors")
    public Director update(@RequestBody Director director) {
        director = ValidatorControllers.validateDirector(director);
        Director newDirector = filmService.updateDirector(director);
        log.debug("Обновлен режиссёр: {}", newDirector);
        return newDirector;
    }

    @DeleteMapping("/directors/{id}")
    public boolean deleteDirector(@PathVariable long id) {
        if (filmService.deleteDirectorById(id)) {
            log.debug("Удалён режиссёр с id = {}", id);
            return true;
        }
        return false;
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> findSortFilmsByDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        List<Film> films = filmService.findSortFilmsByDirector(directorId, sortBy);
        log.debug("Получен отсортированный список фильмов по {}, " +
                "количество = {}", sortBy, films.size());
        return films;
    }

    @GetMapping("/films/search")
    public List<Film> findSortFilmsBySubstring(@RequestParam String query, @RequestParam String by) {
        List<Film> films = filmService.findSortFilmsBySubstring(query, by);
        log.debug("Получен отсортированный список фильмов, который ищет подстроку {} в {}, " +
                "количество = {}", query, by, films.size());
        return films;
    }

    @GetMapping("/films/common")
    public List<Film> findCommonSortFilms(@RequestParam long userId, @RequestParam long friendId) {
        List<Film> films = filmService.findCommonSortFilms(userId, friendId);
        log.debug("Получен список общих отсортированных фильмов " +
                "у пользователей {} и {}.", userId, friendId);
        return films;
    }

}