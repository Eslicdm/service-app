package com.eslirodrigues.member.member.repository;

import com.eslirodrigues.member.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByManagerId(String managerId);
    Optional<Member> findByEmail(String email);
}