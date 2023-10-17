package com.example.tokenservice.util;

import java.security.Key;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.tokenservice.controller.dto.TokenInfoDto;
import com.example.tokenservice.persist.AccessTokensPersist;
import com.example.tokenservice.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 30분
    private final long ACCESS_TIME = 30 * 60 * 1000L;
    private final long REFRESH_TIME = 2 * 60 * 1000L;

    private final UserDetailsServiceImpl userDetailsService;

    private Key key;

    private final AccessTokensPersist tokensPersist;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public TokenInfoDto createAllToken(String uid) {
        return TokenInfoDto.builder()
                .accessToken(createToken(uid, ACCESS_TIME))
                .refreshToken(createToken(uid,0L))
                .build();
    }

    /**
     * 토큰정보 생성
     */
    private String createToken(String uid, Long accessTime) {
        Claims claims = Jwts.claims().setSubject(uid);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // refreshToken 토큰 검증
    // db에 저장되어 있는 token과 비교
    // db에 저장한다는 것이 jwt token을 사용한다는 강점을 상쇄시킨다.
    // public Boolean refreshTokenValidation(String token) {
    //     // DB에 저장한 토큰 비교
    //     AccessTokens refreshToken = this.tokensPersist.findByRefreshToken(token);
    //     if (ObjectUtils.isEmpty(refreshToken)) {
    //         throw new CommonException(Errors.AUTH_TOKEN_NOT_FOUND_ERR);
    //     } else if (refreshToken.getRefreshExpireDate().isBefore(LocalDateTime.now())) {
    //         throw new CommonException(Errors.AUTH_TOKEN_EXPIRE_ERR);
    //     }
    //     return true;
    // }

    // 인증 객체 생성
    public Authentication createAuthentication(String uid) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 uid 가져오는 기능
    public String getUidFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // // 토큰의 유효성 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}