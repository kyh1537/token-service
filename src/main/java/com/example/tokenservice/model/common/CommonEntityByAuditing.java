package com.example.tokenservice.model.common;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateTimeConverter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@EntityListeners(AuditingEntityListener.class)
public abstract class CommonEntityByAuditing implements Persistable<String> {

    @CreatedDate
    @Column(name = "create_date", updatable = false, nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "create_user", updatable = false, nullable = false)
    private String createUser;

    @LastModifiedDate
    @Column(name = "update_date", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updateDate;

    @LastModifiedBy
    @Column(name = "update_user", nullable = false)
    private String updateUser;

    protected void updateUser(String userId) {
        this.updateUser = userId;
        this.updateDate = LocalDateTime.now();
    }

    @Transient
    protected boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
