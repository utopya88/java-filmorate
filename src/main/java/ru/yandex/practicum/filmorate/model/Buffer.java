package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class Buffer {
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

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEFAULT_GENRE = "нет жанра";

    public Map<String, Object> toMapBuffer() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("name", name);
        values.put("description", description);
        values.put("releaseDate", releaseDate);
        values.put("duration", duration);
        values.put("ratingId", mpa);
        return values;
    }

    private Buffer parseObjectNodeToBuffer(ObjectNode objectNode) {
        Long id = objectNode.has("id") ? objectNode.get("id").asLong() : 0L;
        String name = objectNode.get("name").asText();
        String description = objectNode.get("description").asText();
        String releaseDate = objectNode.get("releaseDate").asText();
        Integer duration = objectNode.get("duration").asInt();
        List<String> mpa = objectNode.get("mpa").findValuesAsText("id");
        List<String> genres = extractGenresFromObjectNode(objectNode);

        return Buffer.of(
                id,
                name,
                description,
                LocalDate.parse(releaseDate, DATE_FORMATTER),
                duration,
                genres,
                Long.valueOf(mpa.get(0))
        );
    }

    private List<String> extractGenresFromObjectNode(ObjectNode objectNode) {
        try {
            return objectNode.get("genres").findValuesAsText("id");
        } catch (NullPointerException e) {
           return List.of(DEFAULT_GENRE);
        }
    }
}