package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review save(Review review) {
        log.info("Сохраняем в базе отзыв");
        String sqlQuery = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        log.info(" с id '{}'", review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE REVIEWS SET " +
                "CONTENT = ?, " +
                "IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        log.info("Обновляем отзыв с id '{}'", review.getReviewId());
        return review;
    }

    @Override
    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        log.info("Удаляем отзыв с id '{}'", id);
        return jdbcTemplate.update(sqlQuery, id) > 0L;
    }

    @Override
    public Optional<Review> getReviewById(long id) {
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, SUM(RU.IS_USEFUL) " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_USER RU on R.REVIEW_ID = RU.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ? " +
                "GROUP BY R.REVIEW_ID";
        SqlRowSet reviewsRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (reviewsRows.next()) {
            Review review = Review.builder()
                    .reviewId(reviewsRows.getLong("REVIEW_ID"))
                    .content(reviewsRows.getString("CONTENT"))
                    .isPositive(reviewsRows.getBoolean("IS_POSITIVE"))
                    .userId(reviewsRows.getLong("USER_ID"))
                    .filmId(reviewsRows.getLong("FILM_ID"))
                    .useful(reviewsRows.getLong(6))
                    .build();

            log.info("Найден отзыв с номером {} ", reviewsRows.getInt("REVIEW_ID"));

            return Optional.of(review);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, long count) {
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                "ifnull(SUM(RU.IS_USEFUL), 0) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_USER RU on R.REVIEW_ID = RU.REVIEW_ID " +
                "WHERE R.FILM_ID = ? " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        log.info("Получаем список всех отзывов по фильму с id '{}'", filmId);
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public List<Review> getAllReviewsByParam(long count) {
        String sqlQuery = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USER_ID, R.FILM_ID, " +
                "ifnull(SUM(RU.IS_USEFUL), 0) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_USER RU on R.REVIEW_ID = RU.REVIEW_ID " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";

        log.info("Получаем список всех отзывов");
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    private Review mapRowToReview(ResultSet resultSet, long i) throws SQLException {
        log.info("Создаем объект Отзыв");
        return Review.builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .content(resultSet.getString("CONTENT"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(resultSet.getLong(6))
                .build();
    }

    @Override
    public void putLikeOrDislikeToReview(long id, long userId, long vote) {
        String sqlQuery = "INSERT INTO REVIEW_USER(REVIEW_ID, USER_ID, IS_USEFUL) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId, vote);
        log.info("Добавляем оценку : '{}' к отзыву с id: '{}' от пользователя с id: '{}'", vote, id, userId);
    }

    @Override
    public void deleteLikeOrDislikeToReview(long id, long userId, long vote) {
        String sqlQuery = "DELETE FROM REVIEW_USER " +
                "WHERE REVIEW_ID = ? " +
                "AND USER_ID = ? " +
                "AND IS_USEFUL = ?";
        jdbcTemplate.update(sqlQuery, id, userId, vote);
        log.info("Удаляем оценку : '{}' к отзыву с id: '{}' от пользователя с id: '{}'", vote, id, userId);
    }
}
