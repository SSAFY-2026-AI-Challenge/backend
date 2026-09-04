package com.example.seed.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Authorization 헤더에서 JWT 추출
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 유효한 경우 인증 정보 생성
        if (token != null && jwtTokenProvider.validateToken(token)) {

            Integer memberId =
                    jwtTokenProvider.getMemberId(token);

            String role =
                    jwtTokenProvider.getRole(token);

            // Spring Security 권한 형식
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            // 3. Spring Security 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            memberId.toString(),
                            null,
                            List.of(authority)
                    );

            // 4. 현재 요청의 로그인 사용자로 등록
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        // 5. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    private String resolveToken(
            HttpServletRequest request
    ) {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            return null;
        }

        return authorizationHeader.substring(7);
    }
}