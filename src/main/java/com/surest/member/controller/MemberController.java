package com.surest.member.controller;

import com.surest.member.dto.MemberDto;
import com.surest.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/member")
@AllArgsConstructor
public class MemberController {

    private MemberService memberService;

    // Build Add REST API
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDto> addMember(@RequestBody MemberDto memberDto) {
        MemberDto savedMember = memberService.createMember(memberDto);
        return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }

    // Build Get REST API
    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable UUID uuid) {
        MemberDto memberDetail = memberService.getMemberById(uuid);
        return ResponseEntity.ok(memberDetail);
    }

    // Build Get REST API
    @GetMapping("/members")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<MemberDto>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        // Parse sorting parameter, e.g. "lastName,asc"
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams.length > 1 ? sortParams[1] : "asc");
        Sort sortObj = Sort.by(direction, sortParams[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<MemberDto> memberPage = memberService.getMembersWithFilters(firstName, lastName, pageable);
        return ResponseEntity.ok(memberPage);
    }

    // Build Get all REST API
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<MemberDto>> getAllMembers() {
        List<MemberDto> allMemberDetails = memberService.getAllMemberss();
        return ResponseEntity.ok(allMemberDetails);
    }

    // Build Delete REST API
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMemberById(@PathVariable UUID uuid){
        memberService.deleteMemberById(uuid);
        return ResponseEntity.ok("Member Deleted Successfully");
    }

    // Build Update REST API
    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDto> updateMember(@PathVariable UUID uuid, @RequestBody MemberDto memberDto){
        MemberDto updatedMemberDetails = memberService.updateMember(uuid,memberDto);
        return ResponseEntity.ok(updatedMemberDetails);
    }
}
