package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.save(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("{id}")
    public Film getById(@PathVariable long id) {
        return filmService.getFilmFromStorage(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable("id") long filmId,
                        @PathVariable long userId) {
        return filmService.putLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") long filmId,
                           @PathVariable long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostRatedFilms(@RequestParam(defaultValue = "10") long count,
                                              @RequestParam Optional<Integer> genreId,
                                              @RequestParam Optional<Integer> year) {
        return filmService.getPopular(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getByDirectorId(@PathVariable Integer directorId,
                                      @RequestParam String sortBy) {
        return filmService.getSortedDirectorsFilms(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteById(@PathVariable long filmId) {
        filmService.deleteById(filmId);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId,
                                     @RequestParam long friendId) {
        return filmService.getCommonFilmsByRating(userId, friendId);
    }

    @GetMapping("/search")
    public Collection<Film> getSearchResults(@RequestParam String query,
                                             @RequestParam(defaultValue = "title") Optional<List<String>> by) {
        return filmService.getSearchResults(query, by.get());
    }
}