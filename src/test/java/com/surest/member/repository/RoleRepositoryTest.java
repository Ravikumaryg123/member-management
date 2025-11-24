package com.surest.member.repository;

import com.surest.member.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindByName() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        Role savedRole = roleRepository.save(role);

        Role foundRole = roleRepository.findByName("ROLE_ADMIN");
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getId()).isEqualTo(savedRole.getId());
        assertThat(foundRole.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testFindByName_WhenNotFound_ShouldReturnNull() {
        Role foundRole = roleRepository.findByName("NON_EXISTENT_ROLE");
        assertThat(foundRole).isNull();
    }
}
