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
	private final long REFRESH_TIME = 0L;

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
	public TokenInfoDto createAllToken(String uid) {
		return TokenInfoDto.builder()
			.accessToken(createToken(uid, ACCESS_TIME))
			.refreshToken(createToken(uid, REFRESH_TIME))
			.build();
	}

	/**
	 * 리프레시 토큰을 이용한 접속 토큰 재생성
	 */
	public TokenInfoDto createRefreshToken(String uid, String refreshToken) {
		return TokenInfoDto.builder()
			.accessToken(createToken(uid, REFRESH_TIME))
			.refreshToken(refreshToken)
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
