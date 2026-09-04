package com.example.seed.config;

import com.example.seed.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // JWT 방식이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 세션을 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // 기본 로그인 방식 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth

                        // CORS preflight 요청 허용
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // 로그인 API
                        .requestMatchers(
                                "/api/v1/auth/login"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 서버 헬스체크
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // 에러 처리
                        .requestMatchers(
                                "/error"
                        ).permitAll()


                        // =========================
                        // 학생 전용 API
                        // =========================
                        .requestMatchers(
                                "/api/v1/student/**",
                                "/api/v1/accounts/**",
                                "/api/v1/transactions/**",
                                "/api/v1/payrolls/**",
                                "/api/v1/credit-score/**",
                                "/api/v1/credit-reports/**",
                                "/api/v1/monthly-results/**",

                                "/api/v1/savings/**",
                                "/api/v1/savings-summary/**",
                                "/api/v1/savings-trends/**",
                                "/api/v1/savings-transfers/**",
                                "/api/v1/savings-recommendations/**"
                        ).hasRole("STUDENT")


                        // =========================
                        // 교사 전용 API
                        // =========================
                        .requestMatchers(
                                "/api/v1/classrooms/**"
                        ).hasRole("TEACHER")


                        // =========================
                        // 학생 / 교사 공통
                        // =========================
                        .requestMatchers(
                                "/api/v1/economic-events/**",
                                "/api/v1/me"
                        ).authenticated()


                        // 그 외 API도 로그인 필요
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception

                        // JWT가 없거나 인증되지 않은 경우
                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json;charset=UTF-8"
                                    );

                                    response.getWriter().write(
                                            """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다."
                                            }
                                            """
                                    );
                                }
                        )

                        // 인증은 되었지만 권한이 없는 경우
                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json;charset=UTF-8"
                                    );

                                    response.getWriter().write(
                                            """
                                            {
                                              "code": "FORBIDDEN",
                                              "message": "권한이 없습니다."
                                            }
                                            """
                                    );
                                }
                        )
                )

                // JWT 필터 등록
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}