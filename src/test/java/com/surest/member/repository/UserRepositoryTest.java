package com.surest.member.repository;

import com.surest.member.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByUsernameOrEmail() {
        User user = new User();
        user.setUsername("sandesh");
        user.setEmail("sandesh@example.com");
        user.setPassword("dummypassword");

        User savedUser = userRepository.save(user);

        Optional<User> foundByUsername = userRepository.findByUsernameOrEmail("sandesh", "sandesh");
        assertThat(foundByUsername).isPresent();
        assertThat(foundByUsername.get().getId()).isEqualTo(savedUser.getId());

        Optional<User> foundByEmail = userRepository.findByUsernameOrEmail("sandesh@example.com", "sandesh@example.com");
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void testFindByUsernameOrEmail_WhenNotFound_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findByUsernameOrEmail("notexist", "notexist");
        assertThat(result).isEmpty();
    }
}
