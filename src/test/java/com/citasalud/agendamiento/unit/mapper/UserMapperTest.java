package com.citasalud.agendamiento.unit.mapper;

import com.citasalud.agendamiento.domain.model.User;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.RoleEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.UserEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void shouldMapUserToEntityCorrectly() {
        // Arrange
        User user = new User("test@example.com", "John Doe", "hashed123", (short) 1);
        user.setId(UUID.randomUUID());

        // Act
        UserEntity entity = userMapper.toEntity(user);

        // Assert
        assertNotNull(entity);
        assertEquals(user.getId(), entity.getId());
        assertEquals(user.getEmail(), entity.getEmail());
        assertEquals(user.getNombre(), entity.getNombre());
        assertEquals(user.getHashedPassword(), entity.getHashedPassword());
        assertNotNull(entity.getRole());
        assertEquals(user.getRoleId(), entity.getRole().getRoleId());
    }

    @Test
    void shouldMapEntityToDomainModelCorrectly() {
        // Arrange
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId((short) 2);

        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail("example@test.com");
        entity.setNombre("Jane Doe");
        entity.setHashedPassword("pass456");
        entity.setRole(roleEntity);

        // Act
        User model = userMapper.toDomainModel(entity);

        // Assert
        assertNotNull(model);
        assertEquals(entity.getId(), model.getId());
        assertEquals(entity.getEmail(), model.getEmail());
        assertEquals(entity.getNombre(), model.getNombre());
        assertEquals(entity.getHashedPassword(), model.getHashedPassword());
        assertEquals(roleEntity.getRoleId(), model.getRoleId());
    }
}
