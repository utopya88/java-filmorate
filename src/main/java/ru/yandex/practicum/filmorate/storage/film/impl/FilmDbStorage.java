package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        updateGenres(film);
        updateDirector(film);
        return film;
    }

    private void updateGenres(Film film) {
        if (film.getGenres() != null) {
            String sqlQueryForGenres = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) " +
                    "VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    sqlQueryForGenres, film.getGenres(), film.getGenres().size(),
                    (ps, genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genre.getId());
                    });
        } else film.setGenres(new LinkedHashSet<>());
    }

    private void updateDirector(Film film) {
        if (film.getDirectors() != null) {
            String sqlQueryForDirectors = "INSERT INTO FILM_DIRECTOR(FILM_ID, DIRECTOR_ID) " +
                    "VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    sqlQueryForDirectors, film.getDirectors(), film.getDirectors().size(),
                    (ps, director) -> {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, director.getId());
                    });
        } else film.setDirectors(new LinkedHashSet<>());
    }

    @Override
    public Optional<Film> findFilmById(long id) {
        String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID, " +
                "M.MPA_ID, M.NAME " +
                "FROM FILMS AS F " +
                "JOIN MPA AS M ON F.MPA_ID = M.MPA_ID " +
                "WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (filmRows.next()) {

            Mpa mpa = Mpa.builder()
                    .id(filmRows.getInt(7))
                    .name(filmRows.getString(8))
                    .build();

            Film film = Film.builder()
                    .id(filmRows.getLong(1))
                    .name(filmRows.getString(2))
                    .description(filmRows.getString(3))
                    .releaseDate(LocalDate.parse(filmRows.getString(4)))
                    .duration(filmRows.getInt(5))
                    .mpa(mpa)
                    .genres(new LinkedHashSet<>())
                    .directors(new LinkedHashSet<>())
                    .build();
            loadGenres(Collections.singletonList(film));
            loadDirectors(Collections.singletonList(film));

            log.info("Найден фильм {} с названием {} ", filmRows.getLong(1),
                    filmRows.getString(2));

            return Optional.of(film);
        } else
            return Optional.empty();
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("FILMS.FILM_ID"))
                .name((resultSet.getString("FILMS.NAME")))
                .description(resultSet.getString("FILMS.DESCRIPTION"))
                .duration(resultSet.getInt("FILMS.DURATION"))
                .releaseDate(resultSet.getObject("FILMS.RELEASE_DATE", LocalDate.class))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("MPA.MPA_ID"))
                        .name(resultSet.getString("MPA.NAME"))
                        .build())
                .genres(new LinkedHashSet<>())
                .directors(new LinkedHashSet<>())
                .build();
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS SET " +
                "NAME = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION = ?, " +
                "MPA_ID = ?" +
                "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        String sqlQueryForDeleteGenres = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryForDeleteGenres, film.getId());

        updateGenres(film);

        String sqlQueryForDeleteDirectors = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQueryForDeleteDirectors, film.getId());

        updateDirector(film);
        return film;
    }

    private void loadGenres(List<Film> films) {
        String sqlGenres = "SELECT FILM_ID, G.* " +
                "FROM FILM_GENRE " +
                "JOIN GENRE G ON G.GENRE_ID = FILM_GENRE.GENRE_ID " +
                "WHERE FILM_ID IN (:ids)";

        List<Long> ids = films.stream()
                .map(Film::getId)
                .collect(toList());
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlGenres, parameters);
        while (sqlRowSet.next()) {
            long filmId = sqlRowSet.getLong("FILM_ID");
            int genreId = sqlRowSet.getInt("GENRE_ID");
            String name = sqlRowSet.getString("NAME");
            filmMap.get(filmId).getGenres().add(Genre.builder()
                    .id(genreId)
                    .name(name)
                    .build());
        }
        films.forEach(film -> film.getGenres().addAll(filmMap.get(film.getId()).getGenres()));
    }

    private void loadDirectors(List<Film> films) {
        String sqlDirectors = "SELECT FILM_ID, D.* " +
                "FROM FILM_DIRECTOR " +
                "JOIN DIRECTORS D ON D.DIRECTOR_ID = FILM_DIRECTOR.DIRECTOR_ID " +
                "WHERE FILM_ID IN (:ids)";
        List<Long> ids = films.stream()
                .map(Film::getId)
                .collect(toList());
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlDirectors, parameters);
        while (sqlRowSet.next()) {
            long filmId = sqlRowSet.getLong("FILM_ID");
            long directorId = sqlRowSet.getLong("DIRECTOR_ID");
            String name = sqlRowSet.getString("NAME");
            filmMap.get(filmId).getDirectors().add(Director.builder()
                    .id(directorId)
                    .name(name)
                    .build());
        }
        films.forEach(film -> film.getDirectors().addAll(filmMap.get(film.getId()).getDirectors()));
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT * " +
                "FROM FILMS F " +
                "JOIN MPA M ON F.MPA_ID = M.MPA_ID";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        loadGenres(films);
        loadDirectors(films);
        return films;
    }

    @Override
    public void putLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
    }

    @Override
    public boolean deleteUsersLike(Film film, User user) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE USER_ID = ?";

        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    @Override
    public List<Film> getPopular(long count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Film> films;

        String sqlQueryWithEmpty = "SELECT FILMS.FILM_ID, FILMS.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILMS " +
                "LEFT JOIN FILM_LIKES FL ON FILMS.FILM_ID = FL.FILM_ID " +
                "LEFT JOIN MPA M ON M.MPA_ID = FILMS.MPA_ID " +
                "GROUP BY FILMS.FILM_ID, FL.FILM_ID IN ( " +
                "SELECT FILM_ID " +
                "FROM FILM_LIKES " +
                ") " +
                "ORDER BY COUNT(FL.FILM_ID) DESC " +
                "LIMIT ?";

        String sqlQueryWithGenreId = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILM_GENRE FG " +
                "RIGHT JOIN FILMS F ON F.FILM_ID = FG.FILM_ID " +
                "RIGHT JOIN MPA M on M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN FILM_LIKES FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE FG.GENRE_ID = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(FL.FILM_ID) DESC " +
                "LIMIT ?";

        String sqlQueryWithYear = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILMS F " +
                "RIGHT JOIN MPA M on M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN FILM_LIKES FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE YEAR(F.RELEASE_DATE) = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(FL.FILM_ID) DESC " +
                "LIMIT ?";

        String sqlQueryWithGenreIdAndYear = "SELECT F.FILM_ID, F.NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.NAME " +
                "FROM FILM_GENRE FG " +
                "RIGHT JOIN FILMS F ON F.FILM_ID = FG.FILM_ID " +
                "RIGHT JOIN MPA M on M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN FILM_LIKES FL ON F.FILM_ID = FL.FILM_ID " +
                "WHERE FG.GENRE_ID = ? AND YEAR(F.RELEASE_DATE) = ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(FL.FILM_ID) DESC " +
                "LIMIT ?";


        if (genreId.isPresent() && year.isPresent())
            films = jdbcTemplate.query(sqlQueryWithGenreIdAndYear, this::mapRowToFilm, genreId.get(), year.get(), count);
        else if (genreId.isPresent())
            films = jdbcTemplate.query(sqlQueryWithGenreId, this::mapRowToFilm, genreId.get(), count);
        else if (year.isPresent())
            films = jdbcTemplate.query(sqlQueryWithYear, this::mapRowToFilm, year.get(), count);
        else
            films = jdbcTemplate.query(sqlQueryWithEmpty, this::mapRowToFilm, count);

        loadGenres(films);
        loadDirectors(films);

        return films;
    }

    @Override
    public Collection<Film> getFilmRecommendation(long userWantsRecomId, long userWithCommonLikesId) {
        String findIdSql = "SELECT FILM_ID FROM FILM_LIKES " +
                "WHERE USER_ID = ? " +
                "AND FILM_ID NOT IN (SELECT FILM_ID FROM FILM_LIKES WHERE USER_ID = ?)";

        try {
            return jdbcTemplate.query(findIdSql, (rs, rowNum) -> findFilmById(rs.getLong("FILM_ID"))
                    .orElseThrow(), userWithCommonLikesId, userWantsRecomId);
        } catch (EmptyResultDataAccessException exception) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Film> getCommonFilmsByRating(long userId, long friendId) {

        String sqlQuery =
                "SELECT fi.*, " +
                        "mpa.name, " +
                        "mpa.mpa_id, " +
                        "COUNT(flmlk.film_id) rate " +
                        "FROM films fi " +
                        "JOIN mpa ON fi.mpa_id = mpa.mpa_id " +
                        "JOIN film_likes flmlk ON fi.film_id = flmlk.film_id " +
                        "JOIN film_likes flmlk2 ON fi.film_id = flmlk2.film_id " +
                        "WHERE flmlk.user_id = ? " +
                        "AND flmlk2.user_id = ? " +
                        "GROUP BY fi.film_id " +
                        "ORDER BY rate;";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, friendId);
        loadGenres(films);
        loadDirectors(films);
        return films;
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByYears(long id) {

        String sqlQuery = "SELECT * FROM films f "
                + "JOIN mpa m ON f.mpa_id = m.mpa_id "
                + "WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?) "
                + "ORDER BY EXTRACT(YEAR FROM release_date)";

        log.info("Собираем фильмы");
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        loadGenres(films);
        loadDirectors(films);

        return films;
    }

    @Override
    public List<Film> getSortedDirectorsFilmsByLikes(long id) {
        String sqlQuery =
                "SELECT count(*) , fi.name , fi.film_id , fi.mpa_id , fi.description, fi.release_date, "
                        + "fi.duration, m.name , m.mpa_id  FROM films fi "
                        + "JOIN mpa m ON fi.mpa_id = m.mpa_id "
                        + "LEFT JOIN film_likes fl on fl.film_id = fi.film_id "
                        + "WHERE fi.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?) "
                        + "group by fi.name , fi.film_id , fi.mpa_id , fi.description, fi.release_date, "
                        + "fi.duration, m.name , m.mpa_id "
                        + "ORDER BY count(*) desc";

        log.info("Собираем фильмы");
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        loadGenres(films);
        loadDirectors(films);

        return films;
    }

    public void deleteById(long filmId) {
        String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    public Collection<Film> getSearchResults(String query, List<String> by) {
        String querySourceDbLines = "SELECT FILMS.FILM_ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, " +
                "FILMS.DURATION, FILMS.MPA_ID, MPA.MPA_ID, MPA.NAME, " +
                "D.NAME, " +
                "COUNT(FLMLK.FILM_ID) RATE " +
                "FROM FILMS " +
                "LEFT JOIN MPA ON FILMS.MPA_ID=MPA.MPA_ID " +
                "LEFT JOIN FILM_DIRECTOR FD ON FILMS.FILM_ID=FD.FILM_ID " +
                "LEFT JOIN DIRECTORS D ON FD.DIRECTOR_ID=D.DIRECTOR_ID " +
                "LEFT JOIN FILM_LIKES FLMLK ON FILMS.FILM_ID = FLMLK.FILM_ID ";
        String querySyntax = "%" + query + "%";
        List<Film> films;
        if (by.contains("title") && by.contains("director")) {
            String sqlQuery = querySourceDbLines +
                    "WHERE LOWER(FILMS.NAME) LIKE LOWER(?) " +
                    "OR LOWER(D.NAME) LIKE LOWER(?) " +
                    "GROUP BY FILMS.FILM_ID, FLMLK.FILM_ID " +
                    "ORDER BY RATE DESC;";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, querySyntax, querySyntax);
        } else if (by.contains("director")) {
            String sqlQuery = querySourceDbLines +
                    "WHERE LOWER(D.NAME) LIKE LOWER(?) " +
                    "GROUP BY FILMS.FILM_ID, FLMLK.FILM_ID " +
                    "ORDER BY RATE DESC;";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, querySyntax);
        } else if (by.contains("title")) {
            String sqlQuery = querySourceDbLines +
                    "WHERE LOWER(FILMS.NAME) LIKE LOWER(?) " +
                    "GROUP BY FILMS.FILM_ID, FLMLK.FILM_ID " +
                    "ORDER BY RATE DESC;";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, querySyntax);
        } else {
            log.error("Передан неверный запрос на поиск в by");
            throw new ValidationException("Неверный запрос by");
        }
        log.info("Собрали список через поиск размером в {} элементов", films.size());
        loadGenres(films);
        loadDirectors(films);
        return films;
    }
}