package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director create(Director director) {
        log.info("Добавляем режиссера в коллекцию");
        return directorStorage.save(director);
    }

    public Director findDirectorById(long id) {
        return directorStorage.findDirectorById(id).orElseThrow(() -> new NotFoundException("Режиссер не найден!"));
    }

    public Director update(Director director) {
        log.info("Обновляем режиссера в коллекции");
        findDirectorById(director.getId());
        return directorStorage.update(director);
    }

    public Collection<Director> findAll() {
        log.info("Выводим список всех режиссеров");
        return directorStorage.findAll();
    }

    public void deleteDirectorByID(long id) {
        log.info(String.format("Удаляем режиссера с id: %s", id));
        if (!directorStorage.deleteDirector(id))
            throw new NotFoundException("Режиссер с таким id отсутствует в базе");
    }
}