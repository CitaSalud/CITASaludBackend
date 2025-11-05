package com.citasalud.agendamiento.unit.mapper;


import com.citasalud.agendamiento.domain.model.Role;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.RoleEntity;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RolelMapperTest {
    private final RoleMapper roleMapper = new RoleMapper();

    @Test
    void shouldMapRoleEntityToDomainModelCorrectly() {
        // Arrange
        RoleEntity entity = new RoleEntity();
        entity.setRoleId((short) 2);
        entity.setName("ADMIN");
        entity.setDescription("Administrator role with full permissions");

        // Act
        Role role = roleMapper.toDomainModel(entity);

        // Assert
        assertNotNull(role);
        assertEquals(entity.getRoleId(), role.getId());
        assertEquals(entity.getName(), role.getName());
    }


}
