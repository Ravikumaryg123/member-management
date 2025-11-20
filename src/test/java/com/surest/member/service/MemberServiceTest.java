package com.surest.member.service;

import com.surest.member.dto.MemberDto;
import com.surest.member.entity.Member;
import com.surest.member.exception.ResourceNotFound;
import com.surest.member.mapper.MemberMapper;
import com.surest.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberDto memberDto;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(UUID.randomUUID());
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));
        member.setEmail("john.doe@example.com");

        memberDto = MemberMapper.mapToMemberDto(member);
    }

    @Test
    void createMember_ShouldReturnCreatedMemberDto() {
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberDto createdDto = memberService.createMember(memberDto);

        assertNotNull(createdDto);
        assertEquals(member.getFirstName(), createdDto.getFirstName());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void getMemberById_WhenExists_ShouldReturnMemberDto() {
        UUID id = member.getId();
        when(memberRepository.findById(id)).thenReturn(Optional.of(member));

        MemberDto foundDto = memberService.getMemberById(id);

        assertNotNull(foundDto);
        assertEquals(member.getEmail(), foundDto.getEmail());
        verify(memberRepository, times(1)).findById(id);
    }

    @Test
    void getMemberById_WhenNotExists_ShouldThrowResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound thrown = assertThrows(ResourceNotFound.class,
                () -> memberService.getMemberById(id));
        assertTrue(thrown.getMessage().contains("does not exist"));
    }

//    @Test
//    void getMembersWithFilters_ShouldReturnPagedMemberDto() {
//        Pageable pageable = PageRequest.of(0, 10);
//        List<Member> memberList = List.of(member);
//        Page<Member> memberPage = new PageImpl<>(memberList, pageable, memberList.size());
//
//        when(memberRepository.findAll(any(), eq(pageable))).thenReturn(memberPage);
//
//        Page<MemberDto> dtoPage = memberService.getMembersWithFilters("John", "Doe", pageable);
//
//        assertNotNull(dtoPage);
//        assertEquals(1, dtoPage.getTotalElements());
//        assertEquals(member.getFirstName(), dtoPage.getContent().get(0).getFirstName());
//    }

    @Test
    void getAllMemberss_ShouldReturnListOfMemberDto() {
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberDto> dtoList = memberService.getAllMemberss();

        assertNotNull(dtoList);
        assertFalse(dtoList.isEmpty());
        assertEquals(member.getLastName(), dtoList.get(0).getLastName());
    }

    @Test
    void deleteMemberById_WhenExists_ShouldDeleteMember() {
        UUID id = member.getId();
        when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).deleteById(id);

        assertDoesNotThrow(() -> memberService.deleteMemberById(id));
        verify(memberRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteMemberById_WhenNotExists_ShouldThrowResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound thrown = assertThrows(ResourceNotFound.class,
                () -> memberService.deleteMemberById(id));
        assertTrue(thrown.getMessage().contains("does not exist"));
        verify(memberRepository, never()).deleteById(id);
    }

    @Test
    void updateMember_WhenExists_ShouldReturnUpdatedMemberDto() {
        UUID id = member.getId();
        MemberDto updatedDto = new MemberDto();
        updatedDto.setFirstName("Jane");
        updatedDto.setLastName("Smith");
        updatedDto.setDateOfBirth(LocalDate.of(1985, 5, 15));
        updatedDto.setEmail("jane.smith@example.com");
        updatedDto.setCreatedAt(member.getCreatedAt());
        updatedDto.setUpdatedAt(member.getUpdatedAt());
        updatedDto.setVersion(member.getVersion());

        when(memberRepository.findById(id)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberDto resultDto = memberService.updateMember(id, updatedDto);

        assertEquals("Jane", resultDto.getFirstName());
        assertEquals("Smith", resultDto.getLastName());
        assertEquals("jane.smith@example.com", resultDto.getEmail());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_WhenNotExists_ShouldThrowResourceNotFound() {
        UUID id = UUID.randomUUID();
        MemberDto updatedDto = new MemberDto();

        when(memberRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound thrown = assertThrows(ResourceNotFound.class,
                () -> memberService.updateMember(id, updatedDto));
        assertTrue(thrown.getMessage().contains("does not exist"));
        verify(memberRepository, never()).save(any(Member.class));
    }
}
