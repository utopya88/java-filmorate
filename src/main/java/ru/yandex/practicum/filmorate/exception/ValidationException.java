package ru.yandex.practicum.filmorate.exception;

import lombok.NoArgsConstructor;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
