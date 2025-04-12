package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    /**
     * получить жанр по его идентификатору
     *
     * @param id идентификатор жанра
     * @return объект жанра
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }

    /**
     * получить список всех жанров
     *
     * @return список всех жанров
     */
    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }
}