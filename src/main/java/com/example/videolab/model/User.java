package com.example.videolab.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;

import com.example.videolab.model.common.CommonEntityByAuditing;
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
@Table(name = "user")
public class User extends CommonEntityByAuditing {

    @Id
    @Column(columnDefinition = "varchar(50) NOT NULL COMMENT '사용자 아이디'")
    private String id;

    @Column(columnDefinition = "varchar(50) NOT NULL COMMENT '이메일'", unique = true)
    private String email;

    @Column(columnDefinition = "varchar(60) NOT NULL COMMENT '패스워드'")
    private String password;

    @Column(columnDefinition = "varchar(20) DEFAULT NULL COMMENT '이름'")
    private String name;

    @Column(columnDefinition = "varchar(20) DEFAULT NULL COMMENT '전화번호'")
    private String cellphone;

    @Column(columnDefinition = "varchar(20) DEFAULT 'ACTIVE' COMMENT '사용자 상태'")
    private Status status;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "withdraw_date", columnDefinition = "datetime DEFAULT NULL COMMENT '탈퇴 일시'")
    private LocalDateTime withdrawDate;

    public void updateUserInfo(String email, String name, String cellphone) {
        this.email = email;
        this.name = name;
        this.cellphone = cellphone;
    }

    public void withdraw() {
        this.email = id;
        this.name = null;
        this.cellphone = null;
        this.status = Status.WITHDRAW;
        this.withdrawDate = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE, WITHDRAW
    }
}
