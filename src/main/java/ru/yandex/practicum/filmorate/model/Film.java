package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor(staticName = "of")
public class Film {
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;

    @JsonIgnore
    private Set<Long> likedUsers;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Long mpa;

    private LinkedHashSet<Long> genres;
}