package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmResponse;
import ru.yandex.practicum.filmorate.service.FilmInterface;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEFAULT_GENRE = "нет жанра";

    private final FilmStorage filmStorage;
    private final FilmInterface filmInterface;

    @Autowired
    public FilmController(
            FilmStorage filmStorage,
            FilmInterface filmInterface
    ) {
        this.filmStorage = filmStorage;
        this.filmInterface = filmInterface;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public FilmResponse findById(@PathVariable("id") Long id) {
        return filmStorage.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponse create(@Valid @RequestBody ObjectNode objectNode) {
        return filmStorage.create(FilmDto.parseObjectNodeToBuffer(objectNode));
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody ObjectNode objectNode) {
        return filmStorage.update(FilmDto.parseObjectNodeToBuffer(objectNode));
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmResponse addLike(@Valid @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        return filmInterface.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmResponse delLike(@Valid @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        return filmInterface.delLike(userId, id);
    }

    @GetMapping("/popular")
    public LinkedHashSet<FilmResponse> viewRating(@RequestParam(defaultValue = "10") Long count) {
        return filmInterface.viewRating(count);
    }

}