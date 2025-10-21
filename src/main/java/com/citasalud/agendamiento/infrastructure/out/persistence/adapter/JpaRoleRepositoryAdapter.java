package com.citasalud.agendamiento.infrastructure.out.persistence.adapter;

import com.citasalud.agendamiento.domain.model.Role;
import com.citasalud.agendamiento.domain.ports.out.RoleRepositoryPort;
import com.citasalud.agendamiento.infrastructure.out.persistence.mapper.RoleMapper;
import com.citasalud.agendamiento.infrastructure.out.persistence.repository.SpringDataJpaRoleRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class JpaRoleRepositoryAdapter implements RoleRepositoryPort {
    private final SpringDataJpaRoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public JpaRoleRepositoryAdapter(SpringDataJpaRoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name).map(roleMapper::toDomainModel);
    }

    @Override
    public Optional<Role> findById(Short id) {
        return roleRepository.findById(id).map(roleMapper::toDomainModel);
}
}