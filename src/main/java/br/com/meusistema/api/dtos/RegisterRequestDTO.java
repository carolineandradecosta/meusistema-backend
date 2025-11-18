package br.com.meusistema.api.dtos;

import br.com.meusistema.api.enums.Role;

public record RegisterRequestDTO(
        String username,
        String password,
        String email,
        Role role
) {
}
