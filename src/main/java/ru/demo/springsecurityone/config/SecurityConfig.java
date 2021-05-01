package ru.demo.springsecurityone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.demo.springsecurityone.security.JwtConfigurer;

/**
 * Добавляем провайдер DaoAuthenticationProvider для получения пользователей из БД.
 * Указываем свою реализацию UserDetailsService в конструкторе @Qualifier("userDetailsServiceImpl")
 * и устанавливаем в провайдере
 *
 * @EnableGlobalMethodSecurity(prePostEnabled = true) - устанавливаем что глобально во всём приложении
 * у меня security реализованно в методах (@PreAuthorize)
 *
 * @Bean - добавляем для доступности метода, без него метод не будет работать
 * */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String API_URL = "/api/**";
    private final JwtConfigurer jwtConfigurer;


    // Аутентификация  - это возможноть заходить человеку в приложение. Примером аутентификации может быть
    // сравнение пароля, введенного пользователем, с паролем, который сохранен в базе данных сервера.

    // Авторизация - это проверка на конкретную роль, админ или пользователь,
    // может ли он что-либо делать (заходить на конкретыне разделы, добавлять контент, удалять и тд.).
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // защита от csrf атак отключается для примера с аутентификацией httpBasic
                .csrf().disable()
                // так как мы не используем больше сессии то нужно указать это для sessionManagement
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // первым делом опишем авторизацию на конкретные урлы пользователей
                .authorizeRequests()
                // antMatchers указывает на какие урлы имеет доступ пользователь, permitAll - все пользователи,
                // страница будет доступна так же без аутентификации
                .antMatchers("/", "/api/v1/auth/login").permitAll()
                // далее каждый запрос (anyRequest) по урлам описанным выше (в данном примере "/api/**") должен быть
                // аутентифицирован (authenticated) и использовать
                .anyRequest()
                .authenticated()
                .and()
                // устанавливаем что аутентификация у нас проходит
                // на основании jwtConfigurer
                .apply(jwtConfigurer);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // метод позволяет закодировать пароль с "силой" 12 пароль и получить хэш,
    // не обязательно кодировать но так безопаснее
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}