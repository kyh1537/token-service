package com.example.tokenservice.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tokenservice.model.RefreshTokens;

public interface RefreshTokensRepository extends JpaRepository<RefreshTokens, Long> {

    @Modifying
    @Query("UPDATE RefreshTokens t SET t.isExpire = true, t.expireCause = '신규 로그인' where t.uid = :uid AND t.isExpire = false")
    void expireToken(@Param(value = "uid") String uid);

    RefreshTokens findByRefreshTokenAndIsExpireFalseAndExpireDateAfter(String refreshToken, LocalDateTime date);
}
