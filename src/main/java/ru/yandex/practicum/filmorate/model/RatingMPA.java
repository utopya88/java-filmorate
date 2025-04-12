package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class RatingMPA {
    @NonNull
    private final Long id; // целочисленный идентификатор
    @NotBlank(message = "Ошибка! Название не может быть пустым.")
    private final String name; // название
    @NotBlank(message = "Ошибка! Описание не может быть пустым.")
    private final String description; // описание

}