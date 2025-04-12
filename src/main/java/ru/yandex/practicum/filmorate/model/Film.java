package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@Builder
public class Film {
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;
    private Mpa mpa;
    private LinkedHashSet<Genre> genres;
    private LinkedHashSet<Director> directors;
}