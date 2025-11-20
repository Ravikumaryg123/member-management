package com.surest.member.mapper;

import com.surest.member.dto.MemberDto;
import com.surest.member.entity.Member;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MemberMapperTest {

    @Test
    void testMapToMemberDto() {
        UUID id = UUID.randomUUID();
        LocalDate dob = LocalDate.of(1980, 12, 1);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        Long version = 2l;

        Member member = new Member(
                id,
                "John",
                "Smith",
                dob,
                "john.smith@example.com",
                createdAt,
                updatedAt,
                version
        );

        MemberDto dto = MemberMapper.mapToMemberDto(member);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals(dob, dto.getDateOfBirth());
        assertEquals("john.smith@example.com", dto.getEmail());
        assertEquals(createdAt, dto.getCreatedAt());
        assertEquals(updatedAt, dto.getUpdatedAt());
        assertEquals(version, dto.getVersion());
    }

    @Test
    void testMapToMember() {
        UUID id = UUID.randomUUID();
        LocalDate dob = LocalDate.of(1975, 7, 23);
        LocalDateTime createdAt = LocalDateTime.now().minusWeeks(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(3);
        Long version = 5l;

        MemberDto dto = new MemberDto(
                id,
                "Alice",
                "Brown",
                dob,
                "alice.brown@example.com",
                createdAt,
                updatedAt,
                version
        );

        Member member = MemberMapper.mapToMember(dto);

        assertNotNull(member);
        assertEquals(id, member.getId());
        assertEquals("Alice", member.getFirstName());
        assertEquals("Brown", member.getLastName());
        assertEquals(dob, member.getDateOfBirth());
        assertEquals("alice.brown@example.com", member.getEmail());
        assertEquals(createdAt, member.getCreatedAt());
        assertEquals(updatedAt, member.getUpdatedAt());
        assertEquals(version, member.getVersion());
    }
}
