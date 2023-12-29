package com.example.tokenservice.persist;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.tokenservice.constant.Errors;
import com.example.tokenservice.exception.CommonException;
import com.example.tokenservice.model.RefreshTokens;
import com.example.tokenservice.repository.RefreshTokensRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokensPersist {

	private final RefreshTokensRepository tokensRepository;

	public RefreshTokens findByRefreshToken(String refreshToken) {
		RefreshTokens tokenInfo = this.tokensRepository
			.findByRefreshTokenAndIsExpireFalseAndExpireDateAfter(refreshToken, LocalDateTime.now());

		if (ObjectUtils.isEmpty(tokenInfo)) {
			throw CommonException.of(Errors.AUTH_TOKEN_EXPIRE_ERR);
		}

		return tokenInfo;
	}

	public void expireToken(String uid) {
		this.tokensRepository.expireToken(uid);
	}

	public void save(RefreshTokens refreshToken) {
		this.tokensRepository.save(refreshToken);
	}

}
