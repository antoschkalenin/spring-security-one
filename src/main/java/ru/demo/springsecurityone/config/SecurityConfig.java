package ru.demo.springsecurityone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.demo.springsecurityone.model.Permission;
import ru.demo.springsecurityone.model.Role;

/**
 * @Bean - добавляем для доступности метода, без него метод не будет работать
 * */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String API_URL = "/api/**";

    // Аутентификация  - это возможноть заходить человеку в приложение. Примером аутентификации может быть
    // сравнение пароля, введенного пользователем, с паролем, который сохранен в базе данных сервера.

    // Авторизация - это проверка на конкретную роль, админ или пользователь,
    // может ли он что-либо делать (заходить на конкретыне разделы, добавлять контент, удалять и тд.).
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // защита от csrf атак отключается для примера с аутентификацией httpBasic
                .csrf().disable()
                // первым делом опишем авторизацию на конкретные урлы пользователей
                .authorizeRequests()
                // antMatchers указывает на какие урлы имеет доступ пользователь, permitAll - все пользователи,
                // страница будет доступна так же без аутентификации
                .antMatchers("/").permitAll()

                // Вместо работы на основании ролей (hasAnyRole, hasRole) мы используем hasAuthority.
                // Если у нас будут 50 ролей то в таком подходе нам не нужно будет писать в antMatchers 50 ролей
                .antMatchers(HttpMethod.GET, API_URL).hasAuthority(Permission.DEVELOPERS_READ.getPermission())
                .antMatchers(HttpMethod.POST, API_URL).hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE, API_URL).hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())

                // далее каждый запрос (anyRequest) по урлам описанным выше (в данном примере "/api/**") должен быть
                // аутентифицирован (authenticated) и использовать httpBasic для входа в приложение
                .anyRequest().authenticated().and().httpBasic();
    }

    // Переопределяем метод что бы использовать InMemory users
    // и хранить пользвоателей в приложении.
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // создаем админа с ролью ADMIN, передаём разрешения в authorities
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("123"))
                .authorities(Role.ADMIN.getAuthorities())
                .build();

        // создаем админа с ролью USER, передаём разрешения в authorities
        UserDetails userAnton = User.builder()
                .username("anton")
                .password(passwordEncoder().encode("321"))
                .authorities(Role.USER.getAuthorities())
                .build();

        return new InMemoryUserDetailsManager(admin,userAnton);
    }

    // метод позволяет закодировать пароль с "силой" 12 пароль и получить хэш,
    // не обязательно кодировать но так безопаснее
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}