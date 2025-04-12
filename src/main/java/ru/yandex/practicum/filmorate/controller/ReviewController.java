package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.save(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        reviewService.delete(id);
    }

    @GetMapping("{id}")
    public Review getReview(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviewsByParam(@RequestParam Optional<Long> filmId,
                                             @RequestParam(defaultValue = "10") long count) {
        return reviewService.getAllReviewsByParam(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void putLikeToReview(@PathVariable long id,
                                @PathVariable long userId) {
        long like = 1;
        reviewService.putLikeOrDislikeToReview(id, userId, like);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void putDislikeToReview(@PathVariable long id,
                                   @PathVariable long userId) {
        long dislike = -1;
        reviewService.putLikeOrDislikeToReview(id, userId, dislike);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable long id,
                                   @PathVariable long userId) {
        long like = 1;
        reviewService.deleteLikeOrDislikeToReview(id, userId, like);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable long id,
                                      @PathVariable long userId) {
        long dislike = -1;
        reviewService.deleteLikeOrDislikeToReview(id, userId, dislike);
    }
}
