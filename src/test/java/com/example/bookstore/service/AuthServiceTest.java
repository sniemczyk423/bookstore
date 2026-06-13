package com.example.bookstore.service;

import com.example.bookstore.dto.AuthResponse;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.EmailAlreadyExistsException;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@bookstore.com");
        request.setPassword("user123");
        request.setFirstName("Jan");
        request.setLastName("Kowalski");

        Role role = new Role("USER");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(roleRepository.findByName("USER"))
                .thenReturn(Optional.of(role));

        when(passwordEncoder.encode("user123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthResponse response = authService.register(request);

        assertEquals("user@bookstore.com", response.getEmail());
        assertEquals("USER", response.getRole());

        verify(passwordEncoder).encode("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectExistingEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@bookstore.com");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any());
    }
}