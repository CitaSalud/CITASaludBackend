package com.citasalud.agendamiento.domain.ports.out;

import com.citasalud.agendamiento.domain.model.Role;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findByName(String name);
    Optional<Role> findById(Short id);
}