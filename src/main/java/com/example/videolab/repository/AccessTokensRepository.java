package com.example.videolab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.videolab.model.AccessTokens;

public interface AccessTokensRepository extends JpaRepository<AccessTokens, Long> {

    @Modifying
    @Query("UPDATE AccessTokens t SET t.isExpire = true, t.expireCause = '신규 로그인' where t.uid = :uid AND t.isExpire = false")
    void expireToken(@Param(value = "uid") String uid);

    AccessTokens findByRefreshToken(String refreshToken);
}
