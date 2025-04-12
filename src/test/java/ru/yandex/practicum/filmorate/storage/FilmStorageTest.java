package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmStorageTest {
    private final FilmService filmService;

    private final Mpa mpa = Mpa.builder()
            .id(3)
            .build();

    private final Film film = Film.builder()
            .id(3L)
            .name("film name")
            .description("film description")
            .releaseDate(LocalDate.now().minusYears(10))
            .duration(37)
            .mpa(mpa)
            .build();

    private final Film film1 = Film.builder()
            .id(1L)
            .name("film1 name")
            .description("film1 description")
            .releaseDate(LocalDate.now().minusYears(5))
            .duration(17)
            .mpa(mpa)
            .build();

    @Test
    public void addFilmTest() {
        filmService.save(film);
        AssertionsForClassTypes.assertThat(film).extracting("id").isNotNull();
        AssertionsForClassTypes.assertThat(film).extracting("name").isNotNull();
    }

    @Test
    public void getFilmByIdTest() {
        filmService.save(film);
        Film dbFilm = filmService.getFilmFromStorage(1L);
        assertThat(dbFilm).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void updateFilmTest() {
        Film added = filmService.save(film);
        added.setName("film updated");
        filmService.update(added);
        Film dbFilm = filmService.getFilmFromStorage(added.getId());
        assertThat(dbFilm).hasFieldOrPropertyWithValue("name", "film updated");
    }

    @Test
    public void deleteFilmTest() {
        Film addedFilm = filmService.save(film1);
        Collection<Film> beforeDelete = filmService.findAll();
        filmService.deleteById(addedFilm.getId());
        Collection<Film> afterDelete = filmService.findAll();
        assertEquals(beforeDelete.size() - 1, afterDelete.size());
    }
}