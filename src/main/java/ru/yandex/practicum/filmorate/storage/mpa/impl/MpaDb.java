package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDb implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> findMpaById(int id) {
        String sqlQuery = "SELECT MPA_ID, NAME FROM MPA WHERE MPA_ID = ?";

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(mpaRows.getInt("MPA_ID"))
                    .name(mpaRows.getString("NAME"))
                    .build();
            log.info("Найден рейтинг {} с названием {} ", mpaRows.getInt("MPA_ID"),
                    mpaRows.getString("NAME"));
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с id {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "SELECT MPA_ID, NAME FROM MPA";

        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Mpa mapRowToMpa(ResultSet resultSet, int i) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}