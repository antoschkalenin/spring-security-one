package ru.demo.springsecurityone.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.demo.springsecurityone.model.User;
import ru.demo.springsecurityone.repository.UserRepository;

/**
 * Переопределяем метод получения пользователя из исчтоника, исчтоник БД
 * */
@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // получаем пользователя из БД или кидаем исключение если не нашли
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Такого пользователя нет в БД"));
        return UserDetailsImpl.fromUser(user);
    }
}