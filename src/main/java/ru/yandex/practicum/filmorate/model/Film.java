package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;

@Data
public class Film {

    private long id;
    @NonNull
    @NotBlank(message = "Ошибка! Название не может быть пустым.")
    private String name;
    @NonNull
    private String description;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Ошибка! Продолжительность фильма должна быть положительной.")
    private int duration;
    private int rate;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private RatingMPA mpa;
    private Set<Director> directors = new HashSet<>();
    private Set<Mark> marks = new HashSet<>();

}