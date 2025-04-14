package ru.yandex.practicum.filmorate.model.dto.Film;

import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class FilmDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @Description("Обновление описания")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
    private List<String> genres;
    private Long mpa;
}