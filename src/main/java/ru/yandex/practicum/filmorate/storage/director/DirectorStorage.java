package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {

    Director save(Director director);

    Director update(Director director);

    Collection<Director> findAll();

    Optional<Director> findDirectorById(long id);

    boolean deleteDirector(long id);
}