package com.surest.member.mapper;

import com.surest.member.dto.MemberDto;
import com.surest.member.entity.Member;

public class MemberMapper {

    public static MemberDto mapToMemberDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getDateOfBirth(),
                member.getEmail(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getVersion()
        );
    }

    public static Member mapToMember(MemberDto memberDto) {
        return new Member(
                memberDto.getId(),
                memberDto.getFirstName(),
                memberDto.getLastName(),
                memberDto.getDateOfBirth(),
                memberDto.getEmail(),
                memberDto.getCreatedAt(),
                memberDto.getUpdatedAt(),
                memberDto.getVersion()

        );
    }
}
