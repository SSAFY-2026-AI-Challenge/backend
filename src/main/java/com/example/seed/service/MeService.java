package com.example.seed.service;

import com.example.seed.dto.MeResponse;
import com.example.seed.entity.Member;
import com.example.seed.exception.NotFoundException;
import com.example.seed.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MeService {

    private final MemberRepository memberRepository;

    public MeService(
            MemberRepository memberRepository
    ) {
        this.memberRepository = memberRepository;
    }

    public MeResponse getMe(Integer memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("회원을 찾을 수 없습니다.")
                );

        return new MeResponse(
                member.getId(),
                member.getName(),
                member.getRole(),
                member.getJob(),
                member.getAvatarUrl(),
                member.getClassName(),
                member.getClassroomId()
        );
    }
}