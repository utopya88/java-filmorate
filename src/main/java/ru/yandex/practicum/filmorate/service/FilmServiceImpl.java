package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmResponse;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    // SQL-запросы
    private final String selectTopFilmsQuery = "SELECT f.id as name, COUNT(l.userId) as coun FROM likedUsers as l LEFT OUTER JOIN film AS f ON l.filmId = f.id GROUP BY f.name ORDER BY COUNT(l.userId) DESC LIMIT 10";
    private final String deleteLikeQuery = "DELETE FROM likedUsers WHERE filmId = ? AND userId = ?";
    @Override
    public FilmResponse addLike(Long idUser, Long idFilm) {
        if (userStorage.findById(idUser) != null && filmStorage.findById(idFilm) != null) {
            Map<Long, Set<Long>> likedUsers = filmStorage.selectLikedUsers();
            if (likedUsers.get(idFilm) != null && likedUsers.get(idFilm).contains(idUser)) {
                log.error("Пользователь с ID {} уже поставил лайк фильму с ID {}", idUser, idFilm);
                throw new ConditionsNotMetException("Пользователь с ID " + idUser + " уже поставил лайк фильму с ID " + idFilm);
            } else {
                filmStorage.insertLike(idFilm, idUser);
            }
        }
        FilmResponse film = filmStorage.findById(idFilm);
        LinkedHashSet genres = new LinkedHashSet<>();
        Map<Long, LinkedHashSet<Long>> filmGenre = filmStorage.selectFilmGenre(film.getId());
        if (!filmGenre.isEmpty()) {
            for (Long g : filmGenre.get(film.getId()))
                genres.add(g);
        }
        return FilmResponse.of(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), new HashSet<>(), film.getMpa(), genres);
    }

    @Override
    public FilmResponse delLike(Long idUser, Long idFilm) {
        log.info("Обработка Del-запроса...");
        if (userStorage.findById(idUser) != null && filmStorage.findById(idFilm) != null) {
            Map<Long, Set<Long>> likedUsers = filmStorage.selectLikedUsers();
            if (likedUsers.get(idFilm) != null && !likedUsers.get(idFilm).contains(idUser)) {
                log.error("Пользователь с ID {} не ставил лайк фильму с ID {}", idUser, idFilm);
                throw new ConditionsNotMetException("Пользователь с ID " + idUser + " не ставил лайк фильму с ID " + idFilm);
            } else {
                jdbcTemplate.update(deleteLikeQuery, idFilm, idUser);
            }
        }
        FilmResponse film = filmStorage.findById(idFilm);
        LinkedHashSet genres = new LinkedHashSet<>();
        Map<Long, LinkedHashSet<Long>> filmGenre = filmStorage.selectFilmGenre(film.getId());
        if (!filmGenre.isEmpty()) {
            for (Long g : filmGenre.get(film.getId()))
                genres.add(g);
        }
        return FilmResponse.of(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), new HashSet<>(), film.getMpa(), genres);
    }

    public LinkedHashSet<FilmResponse> viewRating(Long count) {
        log.info("Обработка Get-запроса...");
        LinkedHashMap<Long, Long> likedUsers = jdbcTemplate.query(selectTopFilmsQuery, new TopLikedUsersExtractor());
        LinkedHashSet<FilmResponse> films = new LinkedHashSet<>();
        if (likedUsers == null) {
            log.error("Список фильмов с рейтингом пуст.");
            throw new NotFoundException("Список фильмов с рейтингом пуст.");
        } else {
            LinkedHashSet genres = new LinkedHashSet<>();
            for (Long l : likedUsers.keySet()) {
                Map<Long, LinkedHashSet<Long>> filmGenre = filmStorage.selectFilmGenre(filmStorage.findById(l).getId());
                if (!filmGenre.isEmpty()) {
                    for (Long g : filmGenre.get(filmStorage.findById(l).getId()))
                        genres.add(g);
                }
                films.add(FilmResponse.of(filmStorage.findById(l).getId(), filmStorage.findById(l).getName(), filmStorage.findById(l).getDescription(), filmStorage.findById(l).getReleaseDate(), filmStorage.findById(l).getDuration(), new HashSet<>(), filmStorage.findById(l).getMpa(), genres));
            }
        }
        return films;
    }
}