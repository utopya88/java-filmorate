package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDb implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findGenreById(int id) {
        String sqlQuery = "SELECT GENRE_ID, NAME FROM GENRE WHERE GENRE_ID = ?";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getInt("GENRE_ID"))
                    .name(genreRows.getString("NAME"))
                    .build();
            log.info("Найден жанр {} с названием {} ", genreRows.getInt("GENRE_ID"),
                    genreRows.getString("NAME"));
            return Optional.of(genre);
        } else {
            log.info("Жанр с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT GENRE_ID, NAME FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}