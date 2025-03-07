package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;


@Data
public class Film {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    private int duration;
    private Integer rate = 0;
    private Set<Integer> likes = new HashSet<>();
}
