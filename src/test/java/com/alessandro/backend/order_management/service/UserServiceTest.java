package com.alessandro.backend.order_management.service;

import com.alessandro.backend.order_management.entity.User;
import com.alessandro.backend.order_management.exception.DuplicateEmailException;
import com.alessandro.backend.order_management.exception.UserNotFoundException;
import com.alessandro.backend.order_management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void create_whenEmailNotExists_shouldSaveAndReturnUser() {

        String email = "mario@test.com";
        String name = "Mario";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.create(email, name);

        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(created.getName()).isEqualTo(name);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getName()).isEqualTo(name);

        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    void crate_whenEmailAlreadyExists_shouldThrowAndNotSave() {
        String email = "dup@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> userService.create(email, "Mario"))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining(email);

        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_whenExists_shouldReturnUser() {
        long id = 10L;
        User user = new User("a@test.com", "A");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User found = userService.getById(id);

        assertThat(found).isSameAs(user);
        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_whenMissing_shouldThrowNotFound() {
        long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.valueOf(id));

        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }
}
