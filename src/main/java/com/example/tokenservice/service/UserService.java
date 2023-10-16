package com.example.tokenservice.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.example.tokenservice.constant.Errors;
import com.example.tokenservice.controller.dto.TokenInfoDto;
import com.example.tokenservice.controller.dto.UserDto.LoginDto;
import com.example.tokenservice.controller.dto.UserDto.LoginRes;
import com.example.tokenservice.controller.dto.UserDto.UserCreateDto;
import com.example.tokenservice.controller.dto.UserDto.CreateUserRes;
import com.example.tokenservice.exception.CommonException;
import com.example.tokenservice.util.JwtTokenProvider;
import com.example.tokenservice.model.AccessTokens;
import com.example.tokenservice.model.User;
import com.example.tokenservice.model.User.Status;
import com.example.tokenservice.persist.AccessTokensPersist;
import com.example.tokenservice.persist.UserPersist;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserPersist userPersist;
    private final AccessTokensPersist refreshTokenPersist;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 유저 회원 가입 API 로직
     */
    @Transactional
    public CreateUserRes createUser(UserCreateDto req) {

        this.validationPw(req.getPassword(), req.getCheckPassword());
        this.validationMail(req.getEmail());

        // 유저 정보 생성
        String uid = UUID.randomUUID().toString().toUpperCase();
        User user = User.builder()
                .id(uid)
                .email(req.getEmail())
                .password(this.passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .cellphone(req.getCellPhone())
                .status(Status.ACTIVE)
                .createUser(uid)
                .updateUser(uid)
                .build();

        // 유저 정보 저장
        this.userPersist.save(user);
        return CreateUserRes.builder().id(uid).build();
    }

    // /**
    //  * 유저 정보 수정 API 로직
    //  */
    // @Transactional
    // public void updateUser(String id, UserCreateDto req) {
    //     User originalUser = this.userPersist.findById(id, false);
    //
    //     // 메일이 다르면 체크
    //     if (!req.getEmail().equals(originalUser.getEmail())) {
    //         this.validationMail(req.getEmail());
    //     }
    //
    //     // 유저 정보 수정
    //     originalUser.updateUserInfo(req.getEmail(), req.getName(), req.getCellPhone());
    //     this.userPersist.save(originalUser);
    // }

    // /**
    //  * 사용자 탈퇴 API 로직
    //  */
    // @Transactional
    // public void withdrawUser(String id) {
    //     User originalUser = this.userPersist.findById(id, false);
    //     originalUser.withdraw();
    //     this.userPersist.save(originalUser);
    // }

    @Transactional
    public LoginRes login(LoginDto req) {

        // 아이디 검사
        User user = this.userPersist.findByEmail(req.getEmail()).orElseThrow(
                () -> new CommonException(Errors.USER_NOT_FOUND_ERR)
        );

        // 비밀번호 검사
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CommonException(Errors.PASSWORD_NOT_MATCH_ERR);
        }

        // 기존 토큰 정보 모두 만료 처리
        this.refreshTokenPersist.expireToken(user.getId());

        // Token 생성
        TokenInfoDto tokenDto = this.jwtTokenProvider.createAllToken(user.getId(), null);

        // 신규 토큰 등록
        AccessTokens newToken = AccessTokens.builder()
                .token(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .uid(user.getId())
                .createDate(LocalDateTime.now())
                .refreshExpireDate(LocalDateTime.now().plusDays(1))
                .tokenExpireDate(LocalDateTime.now().plusMinutes(30))
                .isExpire(false)
                .build();
        this.refreshTokenPersist.save(newToken);

        return LoginRes.builder().token(tokenDto).build();
    }

    /**
     * 이메일 중복 체크
     */
    private void validationMail(String email) {
        Optional<User> checkUser = this.userPersist.findByEmail(email);
        if (checkUser.isPresent()) {
            throw new CommonException(Errors.VALIDATION_USER_EMAIL_ERR);
        }
    }

    /**
     * 패스워드 확인 체크
     */
    private void validationPw(String pw1, String pw2) {
        if (ObjectUtils.isEmpty(pw1) || ObjectUtils.isEmpty(pw2) || !pw1.equals(pw2)) {
            throw new CommonException(Errors.PASSWORD_NOT_MATCH_ERR);
        }
    }
}