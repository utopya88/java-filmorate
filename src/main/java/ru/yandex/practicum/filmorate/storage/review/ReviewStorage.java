package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review save(Review review);

    Review update(Review review);

    boolean delete(long id);

    Optional<Review> getReviewById(long id);

    List<Review> getReviewsByFilmId(long filmId, long count);

    List<Review> getAllReviewsByParam(long count);

    void putLikeOrDislikeToReview(long id, long userId, long vote);

    void deleteLikeOrDislikeToReview(long id, long userId, long vote);
}
