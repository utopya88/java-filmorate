package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class Genre {
    @NonNull
    private final Long id;
    @NotBlank(message = "Ошибка! Название не может быть пустым.")
    private final String name;

}