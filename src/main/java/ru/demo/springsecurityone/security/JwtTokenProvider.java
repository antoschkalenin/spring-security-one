package ru.demo.springsecurityone.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.demo.springsecurityone.exception.JwtAuthenticationException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long validityInMilliseconds;
    @Value("${jwt.header}")
    private String authorizationHeader;
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // для безопастности шифруем наш ключ в base64
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // метод для создания токена на основании входящих данных
    public String createToken(String username, String role) {
        // Claims - некая мапа в которую мы можем накидывать кастомные
        // поля которые нам необходимы для создания JWT токена
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        // указываем когда был создан токен
        Date now = new Date();

        // к времени создания токена добавляем милисекунды validityInMilliseconds
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                // нужно подписать токен с помощью алгоритма, например HS256 и добавляем свойсекретный ключ, например: secretKey
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    // метод для валидирования входящего JWT токена
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            // проверяем что дата истчения (expiration) токена находится до текущей даты,
            // простыми словами что токен ещё валиден и не просрочен
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT токен просрочен или не валидный!", HttpStatus.UNAUTHORIZED);
        }
    }
    
    
    // метод для полуения username из токена
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    // получение аутентификации из токена
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // метод для контроллера, получение токена из запроса http
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(authorizationHeader);
    }
}