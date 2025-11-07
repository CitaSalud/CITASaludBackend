package com.citasalud.agendamiento.unit.adapter;

import com.citasalud.agendamiento.domain.model.Role;
import com.citasalud.agendamiento.infrastructure.out.persistence.adapter.JpaRoleRepositoryAdapter;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.RoleMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaRoleRepository;
import com.citasalud.agendamiento.infrastructure.out.persistence.entity.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaRoleRepositoryAdapterTest {


    @Mock
    private SpringDataJpaRoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private JpaRoleRepositoryAdapter adapter;

    private RoleEntity entity;
    private Role domainModel;

    @BeforeEach
    void setUp() {
        entity = new RoleEntity();
        entity.setRoleId((short) 1);
        entity.setName("ADMIN");

        domainModel = new Role();
        domainModel.setId((short) 1);
        domainModel.setName("ADMIN");
    }

    @Test
    void findByName_shouldReturnMappedRole() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(entity));
        when(roleMapper.toDomainModel(entity)).thenReturn(domainModel);

        Optional<Role> result = adapter.findByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
        verify(roleRepository).findByName("ADMIN");
        verify(roleMapper).toDomainModel(entity);
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotFound() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        Optional<Role> result = adapter.findByName("ADMIN");

        assertTrue(result.isEmpty());
        verify(roleRepository).findByName("ADMIN");
        verifyNoInteractions(roleMapper);
    }

    @Test
    void findById_shouldReturnMappedRole() {
        when(roleRepository.findById((short) 1)).thenReturn(Optional.of(entity));
        when(roleMapper.toDomainModel(entity)).thenReturn(domainModel);

        Optional<Role> result = adapter.findById((short) 1);

        assertTrue(result.isPresent());
        assertEquals(domainModel.getId(), result.get().getId());
        verify(roleRepository).findById((short) 1);
        verify(roleMapper).toDomainModel(entity);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(roleRepository.findById((short) 1)).thenReturn(Optional.empty());

        Optional<Role> result = adapter.findById((short) 1);

        assertTrue(result.isEmpty());
        verify(roleRepository).findById((short) 1);
        verifyNoInteractions(roleMapper);
    }


}
