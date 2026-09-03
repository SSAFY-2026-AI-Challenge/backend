package com.example.seed.repository;

import com.example.seed.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    List<Member> findByClassroomIdAndRole(
            Integer classroomId,
            String role
    );

    Optional<Member> findByLoginId(String loginId);
}