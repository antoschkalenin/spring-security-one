package ru.demo.springsecurityone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.demo.springsecurityone.model.Developer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("api/v1/developers")
public class DeveloperController {
    private static List<Developer> DEVELOPERS = Stream.of(
            new Developer(1L, "Anton", "Klenin"),
            new Developer(2L, "Vlad", "Zhuravlev"),
            new Developer(3L, "Kirill", "Brykin")
    ).collect(Collectors.toList());

    @GetMapping
    public List<Developer> getALl() {
        return DEVELOPERS;
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable Long id) {
        return DEVELOPERS.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * @RequestBody - позволяет принимать данные из тела запроса
     * */
    @PostMapping
    public Developer create(@RequestBody Developer developer) {
        DEVELOPERS.add(developer);
        log.info("create developer: {}, state: {}", developer, DEVELOPERS);
        return developer;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        DEVELOPERS.removeIf(d -> d.getId().equals(id));
        log.info("delete by id: {}, state: {}", id, DEVELOPERS);
    }
}