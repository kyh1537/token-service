package com.example.tokenservice.filter;

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

import com.example.tokenservice.dto.UserDetailsImpl;
import com.example.tokenservice.model.User;
import com.example.tokenservice.util.JwtTokenProvider;
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
            request.setAttribute("tokenInfo", getAuthentication());
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

    public void setAuthentication(String uid) {
        Authentication authentication = this.jwtTokenProvider.createAuthentication(uid);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public User getAuthentication() {
        UserDetailsImpl userDetail = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetail.getUser();
    }
}
