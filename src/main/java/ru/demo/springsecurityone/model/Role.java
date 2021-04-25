package ru.demo.springsecurityone.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Связываем роли с разрешениями, теперь у ролей есть
 * свои разрешения на какие либо действие
 * */
public enum Role {
    USER(Set.of(Permission.DEVELOPERS_READ)),
    ADMIN(Set.of(Permission.DEVELOPERS_READ, Permission.DEVELOPERS_WRITE));

    private final Set<Permission> permissions;

    // добавляем конструктор что бы связать роль с разрешениями
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    // для получения разрешений
    public Set<Permission> getPermissions() {
        return permissions;
    }

    // У SS есть GrantedAuthority (interface), в нём описываются права в SS,
    // там есть реализация SimpleGrantedAuthority - эта сущность позволяет
    // определить кто и к чему имеет доступ.
    // В этом методе мы конвертируем наши роли и разрешение в SimpleGrantedAuthority.
    // Теперь SS знает о наших ролях и разрешениях у ролей
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}