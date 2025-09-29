package com.eslirodrigues.member.service;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembersByManagerId(Long managerId) {
        return memberRepository.findAllByManagerId(managerId);
    }

    public Member createMember(
            Long managerId,
            CreateMemberRequest request
    ) {
        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        member.setBirthDate(request.birthDate());
        member.setPhoto(request.photo());
        member.setServiceType(request.serviceType());
        member.setManagerId(managerId);

        return memberRepository.save(member);
    }
}