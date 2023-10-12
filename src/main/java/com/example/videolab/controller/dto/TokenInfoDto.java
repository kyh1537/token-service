package com.example.videolab.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenInfoDto {

    private String accessToken;
    private String refreshToken;
}

