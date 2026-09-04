package com.example.seed.service;

import com.example.seed.dto.LoginRequest;
import com.example.seed.dto.LoginResponse;
import com.example.seed.entity.Member;
import com.example.seed.exception.BadRequestException;
import com.example.seed.repository.MemberRepository;
import com.example.seed.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            MemberRepository memberRepository,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {

        // 1. loginId로 회원 조회
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "아이디 또는 비밀번호가 올바르지 않습니다."
                        )
                );

        // 2. 비밀번호 확인
        // TODO: 추후 BCrypt 적용 시 PasswordEncoder.matches()로 변경
        if (!request.getPassword().equals(member.getPw())) {
            throw new BadRequestException(
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }

        // 3. JWT Access Token 생성
        String accessToken =
                jwtTokenProvider.createAccessToken(
                        member.getId(),
                        member.getRole()
                );

        // 4. 로그인 사용자 정보 생성
        LoginResponse.UserResponse user =
                new LoginResponse.UserResponse(
                        member.getId(),
                        member.getName(),
                        member.getRole(),
                        member.getJob(),
                        member.getAvatarUrl(),
                        member.getClassName(),
                        member.getClassroomId()
                );

        // 5. Access Token + 사용자 정보 반환
        return new LoginResponse(
                accessToken,
                user
        );
    }
}