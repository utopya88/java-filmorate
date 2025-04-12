package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;

@Data
public class User {

    private long id;
    private String name;
    @NonNull
    @NotBlank
    @Email(message = "Ошибка! Неверный e-mail.")
    private String email;
    @NonNull
    @NotBlank(message = "Ошибка! Логин не может быть пустым.")
    private String login;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

}