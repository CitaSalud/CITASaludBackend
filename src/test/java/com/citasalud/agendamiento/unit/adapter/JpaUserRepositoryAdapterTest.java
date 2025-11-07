package com.citasalud.agendamiento.unit.adapter;

import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.infrastructure.out.persistence.adapter.JpaUserRepositoryAdapter;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.UserEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.UserMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryAdapterTest {

    @Mock
    private SpringDataJpaUserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JpaUserRepositoryAdapter adapter;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "Test User", "password123", (short) 1);
        user.setId(UUID.randomUUID());

        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setNombre("Test User");
        userEntity.setHashedPassword("password123");
    }

    @Test
    void save_ShouldReturnMappedUser_WhenSaveIsSuccessful() {
        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomainModel(userEntity)).thenReturn(user);

        User result = adapter.save(user);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userMapper).toEntity(user);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDomainModel(userEntity);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomainModel(userEntity)).thenReturn(user);

        Optional<User> result = adapter.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toDomainModel(userEntity);
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("notfound@example.com");

        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("notfound@example.com");
        verify(userMapper, never()).toDomainModel(any());
    }
}

