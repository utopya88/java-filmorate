package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmDTO;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmDbStorageTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    void createFilmWithEmptyNameShouldThrowException() {
        FilmDTO filmDTO = FilmDTO.of(
                null, // id
                "", // Пустое название
                "Valid description",
                LocalDate.of(2000, 1, 1),
                120,
                Collections.singletonList("1"),
                1L
        );

        assertThrows(ConditionsNotMetException.class, () -> filmDbStorage.create(filmDTO));
    }

    @Test
    void createFilmWithLongDescriptionShouldThrowException() {
        FilmDTO filmDTO = FilmDTO.of(
                null, // id
                "Valid Name",
                "A".repeat(201), // Описание длиннее 200 символов
                LocalDate.of(2000, 1, 1),
                120,
                Collections.singletonList("1"),
                1L
        );


        assertThrows(ConditionsNotMetException.class, () -> filmDbStorage.create(filmDTO));
    }

    @Test
    void createFilmWithInvalidReleaseDateShouldThrowException() {
        FilmDTO filmDTO = FilmDTO.of(
                null, // id
                "Valid Name",
                "Valid description",
                LocalDate.of(1890, 1, 1), // Дата раньше 28 декабря 1895 года
                120,
                Collections.singletonList("1"),
                1L
        );
        assertThrows(ConditionsNotMetException.class, () -> filmDbStorage.create(filmDTO));
    }

    @Test
    void createFilmWithNegativeDurationShouldThrowException() {
        FilmDTO filmDTO = FilmDTO.of(
                null, // id
                "Valid Name",
                "Valid description",
                LocalDate.of(2000, 1, 1),
                -120, // Отрицательная продолжительность
                Collections.singletonList("1"),
                1L
        );

        assertThrows(ConditionsNotMetException.class, () -> filmDbStorage.create(filmDTO));
    }

    @Test
    void createFilmWithInvalidMpaShouldThrowException() {
        FilmDTO filmDTO = FilmDTO.of(
                null, // id
                "Valid Name",
                "Valid description",
                LocalDate.of(2000, 1, 1),
                120,
                Collections.singletonList("1"),
                0L // Некорректный рейтинг MPA
        );

        assertThrows(NotFoundException.class, () -> filmDbStorage.create(filmDTO));
    }
}