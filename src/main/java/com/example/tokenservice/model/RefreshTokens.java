package com.example.tokenservice.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokens {

	@Id
	@Column(columnDefinition = "bigint(20) NOT NULL COMMENT '고유 인덱스'")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "refresh_token", columnDefinition = "varchar(500) NOT NULL COMMENT '리프레시 토큰'")
	private String refreshToken;

	@Column(name = "uid", columnDefinition = "varchar(50) NOT NULL COMMENT '사용자 고유 아이디'")
	private String uid;

	@Convert(converter = LocalDateTimeConverter.class)
	@Column(name = "create_date", columnDefinition = "datetime NOT NULL COMMENT '토큰 생성 일시'")
	private LocalDateTime createDate;

	@Convert(converter = LocalDateTimeConverter.class)
	@Column(name = "expire_date", columnDefinition = "datetime DEFAULT NULL COMMENT '리프레시 토큰 만료 시간'")
	private LocalDateTime expireDate;

	@Column(name = "is_expire", columnDefinition = "bit(1) NOT NULL DEFAULT b'0' COMMENT '만료 여부'")
	private Boolean isExpire;

	@Column(name = "expire_cause", columnDefinition = "varchar(20) DEFAULT NULL COMMENT '만료 사유'")
	private String expireCause;

	public static RefreshTokens of(String refreshToken, String uid) {
		return RefreshTokens.builder()
			.refreshToken(refreshToken)
			.uid(uid)
			.createDate(LocalDateTime.now())
			.expireDate(LocalDateTime.now().plusDays(1))
			.isExpire(false)
			.build();
	}
}

