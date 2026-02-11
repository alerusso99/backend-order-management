package com.alessandro.backend.order_management.repository;

import com.alessandro.backend.order_management.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_whenExists_shouldReturnUser() {
        User user = new User("repo@test.com", "Repo");

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("repo@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("repo@test.com");
        assertThat(found.get().getName()).isEqualTo("Repo");
    }

    @Test
    void findByEmail_whenMissing_shouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("missing@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    void save_duplicateEmail_shouldFail() {
        User u1 = new User("unique@test.com", "One");
        userRepository.save(u1);
        User u2 = new User("unique@test.com", "Two");

        assertThatThrownBy(() -> userRepository.saveAndFlush(u2))
            .isInstanceOf(Exception.class);
    }
}
