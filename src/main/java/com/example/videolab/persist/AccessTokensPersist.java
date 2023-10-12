package com.example.videolab.persist;

import org.springframework.stereotype.Service;

import com.example.videolab.model.AccessTokens;
import com.example.videolab.repository.AccessTokensRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokensPersist {

    private final AccessTokensRepository refreshTokenRepository;

    public AccessTokens findByRefreshToken(String refreshToken) {
        return this.refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public void expireToken(String uid) {
        this.refreshTokenRepository.expireToken(uid);
    }

    public void save(AccessTokens refreshToken) {
        this.refreshTokenRepository.save(refreshToken);
    }

}
