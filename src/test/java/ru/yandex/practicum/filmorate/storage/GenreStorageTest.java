package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreStorageTest {
    private final GenreService genreService;

    @Test
    public void getAllGenresTest() {
        Collection<Genre> genre = genreService.getAllGenres();
        Assertions.assertThat(genre)
                .extracting(Genre::getName)
                .containsAll(Arrays.asList("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"));
    }

    @Test
    public void getAllGenresSizeTest() {
        Collection<Genre> genres = genreService.getAllGenres();
        assertEquals(6, genres.size());
    }

    @Test
    public void getGenreByIdTest() {
        Genre genre = genreService.getGenreById(4);
        assertEquals("Триллер", genre.getName());
    }
}