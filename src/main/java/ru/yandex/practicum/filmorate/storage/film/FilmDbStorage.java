package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.FindObjectException;
import ru.yandex.practicum.filmorate.model.*;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rate", film.getRate());

        film.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        return Optional.of(film);
    }

    public Optional<Film> update(Film film) {
        String sqlQuery = "UPDATE FILMS set film_name = ?, description = ?, release_date = ?, " +
                "duration = ?, rate = ?, rating_mpa_id = ? where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getId());

        return Optional.of(film);
    }

    public boolean delete(Film film) {
        String sqlQuery = "delete from films where film_id = ?";
        return jdbcTemplate.update(sqlQuery, film.getId()) > 0;
    }

    public boolean deleteFilmById(long filmId) {
        String sqlQuery = "delete from films where film_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }

    public List<Film> findAll() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    public Film findFilmById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Фильм № {} не найден", filmId);
            throw new FindObjectException(String.format("Фильм с идентификатором № %d не найден", filmId));
        }
    }

    public boolean isFindFilmById(Integer filmId) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        if (jdbcTemplate.queryForObject(sqlQuery, Boolean.class, filmId)) {
            return true;
        }
        log.warn("Фильм № {} не найден", filmId);
        throw new FindObjectException(String.format("Фильм № %d не найден", filmId));
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getString("film_name"), rs.getString("description"),
                rs.getDate("release_date").toLocalDate());
        film.setId(rs.getInt("film_id"));
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        return film;
    }
}
