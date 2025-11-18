package br.com.meusistema.api.dtos;

import br.com.meusistema.api.enums.Role;

public record UsuarioResponseDTO(
        String username,
        String email,
        Role role
) {
}
