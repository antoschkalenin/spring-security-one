package ru.demo.springsecurityone.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.demo.springsecurityone.model.Status;
import ru.demo.springsecurityone.model.User;

import java.util.Collection;
import java.util.List;

/**
 * Определяем своего пользователя используя интерфейс UserDetails SS.
 * Добавляем поля username, password, разрешения authorities и isActive,
 * инцилизируем в конструкторе и возвращаем из методов.
 * И главное создаём изз пользователя в Бд пользователя SS.
 * */
@Data
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public UserDetailsImpl(String username, String password, List<SimpleGrantedAuthority> authorities, boolean isActive) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    // Нужно связать пользователя из БД с пользователем SS в приложении.
    // Из данных пользователя в БД создаём User SS.
    public static UserDetails fromUser(User user) {
        return new org.springframework.security.core.userdetails.User(
          user.getEmail(), user.getPassword(),
          user.getStatus().equals(Status.ACTIVE),
          user.getStatus().equals(Status.ACTIVE),
          user.getStatus().equals(Status.ACTIVE),
          user.getStatus().equals(Status.ACTIVE),
          user.getRole().getAuthorities()
        );
    }
}