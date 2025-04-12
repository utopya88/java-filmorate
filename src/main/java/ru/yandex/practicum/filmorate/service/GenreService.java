package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.findAll();
    }

    public Genre getGenreById(int id) {
        return genreStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден!"));
    }
}