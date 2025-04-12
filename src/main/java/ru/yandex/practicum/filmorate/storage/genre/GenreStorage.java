package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> findGenreById(int id);

    List<Genre> findAll();

    Genre mapRowToGenre(ResultSet resultSet, int i) throws SQLException;
}