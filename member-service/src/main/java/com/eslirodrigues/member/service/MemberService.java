package com.eslirodrigues.member.service;

import com.eslirodrigues.member.dto.CreateMemberRequest;
import com.eslirodrigues.member.dto.UpdateMemberRequest;
import com.eslirodrigues.member.entity.Member;
import com.eslirodrigues.member.exception.DuplicateEmailException;
import jakarta.persistence.EntityNotFoundException;
import com.eslirodrigues.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> getAllMembersByManagerId(String managerId) {
        return memberRepository.findAllByManagerId(managerId);
    }

    public Member getMemberById(String managerId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new EntityNotFoundException("Member not found with id: " + memberId));

        if (!member.getManagerId().equals(managerId)) {
            throw new AccessDeniedException("Not authorized to access this member");
        }

        return member;
    }

    public Member createMember(
            String managerId,
            CreateMemberRequest request
    ) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException("Member creation failed due to a conflict");
        }

        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        member.setBirthDate(request.birthDate());
        member.setPhoto(request.photo());
        member.setServiceType(request.serviceType());
        member.setManagerId(managerId);

        return memberRepository.save(member);
    }

    public Member updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new EntityNotFoundException("Member not found with id: " + memberId));

        Optional.ofNullable(request.name()).ifPresent(member::setName);
        Optional.ofNullable(request.email()).ifPresent(member::setEmail);
        Optional.ofNullable(request.birthDate()).ifPresent(member::setBirthDate);
        Optional.ofNullable(request.photo()).ifPresent(member::setPhoto);
        Optional.ofNullable(request.serviceType()).ifPresent(member::setServiceType);

        return memberRepository.save(member);
    }

    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("Member not found with id: " + memberId);
        }
        memberRepository.deleteById(memberId);
    }
}