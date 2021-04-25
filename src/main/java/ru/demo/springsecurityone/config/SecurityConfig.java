package ru.demo.springsecurityone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.demo.springsecurityone.model.Role;

/**
 * @EnableGlobalMethodSecurity(prePostEnabled = true) - устанавливаем что глобально во всём приложении
 * у меня security реализованно в методах (@PreAuthorize)
 *
 * @Bean - добавляем для доступности метода, без него метод не будет работать
 * */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
                // далее каждый запрос (anyRequest) по урлам описанным выше (в данном примере "/api/**") должен быть
                // аутентифицирован (authenticated) и использовать
                .anyRequest()
                .authenticated()
                .and()
                // formLogin - если доабвить только этот метод то будет использоваться страница авторизации по умолчанию.
                .formLogin()
                // loginPage - устанавливаем урл для отображения страницы логина и устанавливаем доступ всем иначе получим ошибку
                .loginPage("/auth/login").permitAll()

                // Если зашли успешно то перенаправляемся по урлу /auth/success.
                // Но если мы перед заходом на страницу логина обращались на какой то ранее
                // авторизованный урл то после авторизации нас перекинет именно туда а не на страницу success,
                // SS запоминает предыдущую попытку обратиться по урлу если не прошли авторизацию и кинет после успешной авторизации
                .defaultSuccessUrl("/auth/success")

                // и настроим страницу logout
                // (по умолчанию он работает по методу GET /logout, что не безопастно (написано в документации SS)
                .and()
                .logout()
                // а именно по урлу "/auth/logout" и с методом POST
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                // сделать не валидным сессию
                .invalidateHttpSession(true)
                // очистить аутентификацию
                .clearAuthentication(true)
                // удаляем куки под названием JSESSIONID
                .deleteCookies("JSESSIONID")
                // после выхода перенаправить на страницу логина
                .logoutSuccessUrl("/auth/login");
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