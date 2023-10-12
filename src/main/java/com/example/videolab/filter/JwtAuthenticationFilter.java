package com.example.videolab.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.videolab.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = this.resolveToken(request);

        if (!ObjectUtils.isEmpty(accessToken) && this.jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(this.jwtTokenProvider.getUidFromToken(accessToken));
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // SecurityContext 에 Authentication 객체를 저장
    public void setAuthentication(String uid) {
        Authentication authentication = this.jwtTokenProvider.createAuthentication(uid);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}