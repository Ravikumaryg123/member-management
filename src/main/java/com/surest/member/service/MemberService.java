package com.surest.member.service;

import com.surest.member.dto.MemberDto;
import com.surest.member.entity.Member;
import com.surest.member.exception.BusinessServiceException;
import com.surest.member.exception.ResourceNotFound;
import com.surest.member.mapper.MemberMapper;
import com.surest.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;

    @Transactional
    public MemberDto createMember(MemberDto memberDto) throws BusinessServiceException {
        if (memberRepository.existsByEmail(memberDto.getEmail())) {
            throw new BusinessServiceException("Email already exists", HttpStatus.CONFLICT);
        }
        Member member = MemberMapper.mapToMember(memberDto);
        Member savedMember = memberRepository.save(member);
        return MemberMapper.mapToMemberDto(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberById(UUID uuid) {
        Member memberDetail = memberRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFound("Member with given id " + uuid + " does not exist."));
        return MemberMapper.mapToMemberDto(memberDetail);
    }

    @Transactional(readOnly = true)
    public Page<MemberDto> getMembersWithFilters(String firstName, String lastName, Pageable pageable) {
        Specification<Member> spec = Specification.unrestricted();
        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }
        // Retrieve paged result of Member entities
        Page<Member> memberPage = memberRepository.findAll(spec, pageable);
        // Convert entities to DTOs
        List<MemberDto> dtoList = memberPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        // Return a new Page object with DTOs, preserving pagination info
        return new PageImpl<>(dtoList, pageable, memberPage.getTotalElements());
    }

    private MemberDto convertToDto(Member member) {
        // Mapping logic from Member entity to MemberDto here
        return MemberMapper.mapToMemberDto(member);
    }

    @Transactional(readOnly = true)
    public List<MemberDto> getAllMemberss() {
        List<Member> allMembers = memberRepository.findAll();
        List<MemberDto> allMemberDetails = allMembers.stream().map((member) -> MemberMapper.mapToMemberDto(member)).collect(Collectors.toList());
        return allMemberDetails;
    }

    @Transactional
    public void deleteMemberById(UUID uuid) throws BusinessServiceException {
        if (!memberRepository.existsById(uuid)) {
            throw new BusinessServiceException("Member not found", HttpStatus.NOT_FOUND);
        }
        memberRepository.deleteById(uuid);
    }

    @Transactional
    public MemberDto updateMember(UUID uuid, MemberDto updatedMember) {
        Member member = memberRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFound("Member does not exist with the given Id "+uuid));
        member.setFirstName(updatedMember.getFirstName());
        member.setLastName(updatedMember.getLastName());
        member.setDateOfBirth(updatedMember.getDateOfBirth());
        member.setEmail(updatedMember.getEmail());
        member.setUpdatedAt(updatedMember.getUpdatedAt());
        member.setCreatedAt(updatedMember.getCreatedAt());
        member.setVersion(updatedMember.getVersion());

        Member updatedMemberDetails = memberRepository.save(member);
        return MemberMapper.mapToMemberDto(updatedMemberDetails);
    }

}
