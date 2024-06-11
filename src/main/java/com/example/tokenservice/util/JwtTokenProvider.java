package com.example.tokenservice.util;

import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.tokenservice.controller.dto.TokenInfoDto;
import com.example.tokenservice.model.User;
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

	// 토큰 유효 시간 30분
	private final long ACCESS_TIME = 30 * 60 * 1000L;
	private final long REFRESH_TIME = 24 * 3600 * 1000L;

	private final UserDetailsServiceImpl userDetailsService;

	private Key key;

	@PostConstruct
	protected void init() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 로그인시 토큰 생성
	 */
	public TokenInfoDto createAllToken(User user) {
		return TokenInfoDto.builder()
			.accessToken(createToken(user, ACCESS_TIME, "access"))
			.refreshToken(createToken(user, REFRESH_TIME, "refresh"))
			.build();
	}

	/**
	 * 리프레시 토큰을 이용한 접속 토큰 재생성
	 */
	public TokenInfoDto createRefreshToken(User user, String refreshToken) {
		return TokenInfoDto.builder()
			.accessToken(createToken(user, ACCESS_TIME, "access"))
			.refreshToken(refreshToken)
			.build();
	}

	/**
	 * 토큰정보 생성
	 */
	private String createToken(User user, Long accessTime, String type) {
		Claims claims = Jwts.claims();
		claims.put("uid", user.getId());
		claims.put("email", user.getEmail());
		claims.put("name", user.getName());

		Date now = new Date();

		return Jwts.builder()
			.setHeaderParam("type", type)
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + accessTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Authentication createAuthentication(String uid) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(uid);
		return UsernamePasswordAuthenticationToken.authenticated(userDetails, "", userDetails.getAuthorities());
	}

	public String getUidFromToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get("uid", String.class);
	}

	public boolean validateToken(String jwtToken) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);

			// refresh token 접근시 false
			if (claims.getHeader().get("type").equals("refresh")) {
				return false;
			}

			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
}
