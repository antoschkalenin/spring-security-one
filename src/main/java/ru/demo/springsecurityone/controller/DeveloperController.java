package ru.demo.springsecurityone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.demo.springsecurityone.model.Developer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/developers")
public class DeveloperController {
    private final List<Developer> developers = Stream.of(
            new Developer(1L, "Anton", "Klenin"),
            new Developer(2L, "Vlad", "Zhuravlev"),
            new Developer(3L, "Kirill", "Brykin")
    ).collect(Collectors.toList());

    @GetMapping
    public List<Developer> getALl() {
        return developers;
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable Long id) {
        return developers.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}