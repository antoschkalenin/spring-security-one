package ru.demo.springsecurityone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.demo.springsecurityone.model.Developer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Авторизация пользователей на основании прав (authorities)
 *
 * Что бы в конфиге не писать каждый раз много правил
 * .antMatchers(HttpMethod.GET, API_URL).hasAuthority(Permission.DEVELOPERS_READ.getPermission()) и тд.
 * можно воспользваоть его аналогом и раскидать права по контроллерам и методам контроллеров, например:
 * @PreAuthorize("hasAuthority('developers:read')") будет аналогом строки выше.
 * Для включения @PreAuthorize в конфиге нужно добавить @EnableGlobalMethodSecurity(prePostEnabled = true) на класс
 * */
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
    @PreAuthorize("hasAuthority('developers:read')")
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
    @PreAuthorize("hasAuthority('developers:write')")
    public Developer create(@RequestBody Developer developer) {
        DEVELOPERS.add(developer);
        log.info("create developer: {}, state: {}", developer, DEVELOPERS);
        return developer;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:write')")
    public void deleteById(@PathVariable Long id) {
        DEVELOPERS.removeIf(d -> d.getId().equals(id));
        log.info("delete by id: {}, state: {}", id, DEVELOPERS);
    }
}