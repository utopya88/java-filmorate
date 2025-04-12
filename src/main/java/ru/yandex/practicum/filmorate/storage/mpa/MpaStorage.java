package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> findMpaById(int id);

    Collection<Mpa> findAll();

    Mpa mapRowToMpa(ResultSet resultSet, int i) throws SQLException;

}