package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    /**
     * получить рейтинг mpa по его идентификатору
     *
     * @param id идентификатор рейтинга mpa
     * @return объект рейтинга mpa
     */
    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return mpaService.getMpaById(id);
    }

    /**
     * получить список всех рейтингов mpa
     *
     * @return список всех рейтингов mpa
     */
    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }
}