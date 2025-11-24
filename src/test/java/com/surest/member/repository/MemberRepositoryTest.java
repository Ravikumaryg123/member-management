package com.surest.member.repository;

import com.surest.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndFindById() {
        Member member = new Member();
        member.setFirstName("Hemanth");
        member.setLastName("Kumar");
        member.setEmail("hemanth@example.com");
        member.setDateOfBirth(LocalDate.of(1992, 3, 15));

        Member savedMember = memberRepository.save(member);

        Optional<Member> retrieved = memberRepository.findById(savedMember.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getFirstName()).isEqualTo("Hemanth");
    }

    @Test
    void testFindAll() {
        memberRepository.save(new Member(null, "Sundar", "B", LocalDate.of(1988, 7, 20),"sundar@example.com", null, null, null));
        memberRepository.save(new Member(null, "Rajeev", "Kale", LocalDate.of(1995, 5, 10),"rajeev@example.com", null, null, null));

        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testDeleteById() {
        Member member = new Member();
        member.setFirstName("Madhu");
        member.setLastName("Sudhan");
        member.setEmail("madhu@example.com");
        member.setDateOfBirth(LocalDate.of(1990, 1, 1));
        Member saved = memberRepository.save(member);

        memberRepository.deleteById(saved.getId());
        Optional<Member> deleted = memberRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindAllWithSpecification() {
        Member member = new Member();
        member.setFirstName("Nagesh");
        member.setLastName("R");
        member.setEmail("naga@example.com");
        member.setDateOfBirth(LocalDate.of(1985, 12, 12));
        memberRepository.save(member);

        Specification<Member> firstNameSpec = (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%nag%");
        List<Member> result = memberRepository.findAll(firstNameSpec);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getFirstName()).isEqualToIgnoringCase("Nag");
    }
}
