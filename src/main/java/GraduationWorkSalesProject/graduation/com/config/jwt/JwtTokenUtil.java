package GraduationWorkSalesProject.graduation.com.config.jwt;

import GraduationWorkSalesProject.graduation.com.exception.ExpiredRefreshTokenException;
import GraduationWorkSalesProject.graduation.com.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.access.validity}")
    private long ACCESS_TOKEN_VALIDITY;
    @Value("${jwt.refresh.validity}")
    private long REFRESH_TOKEN_VALIDITY;

    @Value("${jwt.access.secret}")
    private String ACCESS_TOKEN_SECRET;
    @Value("${jwt.refresh.secret}")
    private String REFRESH_TOKEN_SECRET;

    public String getUsernameFromAccessToken(String token) {
        return getClaimFromAccessToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromAccessToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromAccessToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromAccessToken(String token) {
        return Jwts.parser().setSigningKey(ACCESS_TOKEN_SECRET).parseClaimsJws(token).getBody();
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "AccessToken");
        return doGenerateToken(claims, headers, userDetails.getUsername(), ACCESS_TOKEN_VALIDITY, ACCESS_TOKEN_SECRET);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "RefreshToken");
        return doGenerateToken(claims, headers, userDetails.getUsername(), REFRESH_TOKEN_VALIDITY, REFRESH_TOKEN_SECRET);
    }

    private String doGenerateToken(Map<String, Object> claims, Map<String, Object> headers, String subject, long validity, String secret) {
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateAccessToken(String token) {
        return validateToken(token, ACCESS_TOKEN_SECRET);
    }

    public void validateRefreshToken(String token) {
        if (!validateToken(token, REFRESH_TOKEN_SECRET))
            throw new ExpiredRefreshTokenException();
    }

    private Boolean validateToken(String token, String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            throw new InvalidJwtException();
        }
        return true;
    }
}
