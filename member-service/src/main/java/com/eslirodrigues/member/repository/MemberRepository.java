package com.eslirodrigues.member.repository;

import com.eslirodrigues.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByManagerId(Long managerId);
    Optional<Member> findByEmail(String email);
}