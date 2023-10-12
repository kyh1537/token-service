package com.example.videolab.util;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.example.videolab.constant.Errors;
import com.example.videolab.controller.dto.TokenInfoDto;
import com.example.videolab.exception.CommonException;
import com.example.videolab.model.AccessTokens;
import com.example.videolab.persist.AccessTokensPersist;
import com.example.videolab.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
    public final String ACCESS_TOKEN = "Access";
    public final String REFRESH_TOKEN = "Refresh";

    private final UserDetailsServiceImpl userDetailsService;

    private Key key;

    private final AccessTokensPersist tokensPersist;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // // header 토큰을 가져오는 기능
    // public String getHeaderToken(HttpServletRequest request, String type) {
    //     return type.equals("Access") ? request.getHeader(ACCESS_TOKEN) : request.getHeader(REFRESH_TOKEN);
    // }

    // 토큰 생성
    public TokenInfoDto createAllToken(String uid, List<String> roles) {
        return TokenInfoDto.builder()
                .accessToken(createToken(uid, "Access", roles))
                .refreshToken(createToken(uid, "Refresh", roles))
                .build();
    }

    /**
     * 토큰정보 생성
     */
    private String createToken(String uid, String type, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(uid);
        claims.put("roles", roles);

        Date now = new Date();
        long accessTime = type.equals("Access") ? ACCESS_TIME : REFRESH_TIME;
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // // 토큰 검증
    // public Boolean tokenValidation(String token) {
    //     try {
    //         return !ObjectUtils.isEmpty(this.verifyToken(token));
    //     } catch (Exception ex) {
    //         return false;
    //     }
    // }

    // refreshToken 토큰 검증
    // db에 저장되어 있는 token과 비교
    // db에 저장한다는 것이 jwt token을 사용한다는 강점을 상쇄시킨다.
    public Boolean refreshTokenValidation(String token) {
        // DB에 저장한 토큰 비교
        AccessTokens refreshToken = this.tokensPersist.findByRefreshToken(token);
        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new CommonException(Errors.AUTH_TOKEN_NOT_FOUND_ERR);
        } else if (refreshToken.getRefreshExpireDate().isBefore(LocalDateTime.now())) {
            throw new CommonException(Errors.AUTH_TOKEN_EXPIRE_ERR);
        }
        return true;
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String uid) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 uid 가져오는 기능
    public String getUidFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    // public Claims verifyToken(String token) {
    //     try {
    //         return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    //     } catch (ExpiredJwtException e) { // 토큰만료
    //         throw new CommonException(Errors.AUTH_TOKEN_EXPIRE_ERR);
    //     } catch (Exception e) { // 그외 오류
    //         throw new CommonException(Errors.AUTH_TOKEN_NOT_FOUND_ERR);
    //     }
    // }

    // // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}