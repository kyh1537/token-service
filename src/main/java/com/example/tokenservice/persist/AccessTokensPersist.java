package com.example.tokenservice.persist;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.tokenservice.model.AccessTokens;
import com.example.tokenservice.repository.AccessTokensRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokensPersist {

	private final AccessTokensRepository refreshTokenRepository;

	public AccessTokens findByRefreshToken(String refreshToken) {
		return this.refreshTokenRepository.findByRefreshTokenAndIsExpireFalseAndRefreshExpireDateAfter(refreshToken,
			LocalDateTime.now());
	}

	public void expireToken(String uid) {
		this.refreshTokenRepository.expireToken(uid);
	}

	public void save(AccessTokens refreshToken) {
		this.refreshTokenRepository.save(refreshToken);
	}

}
