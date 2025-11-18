package br.com.meusistema.api.dtos;

public record LoginRequestDTO(
        String username,
        String password
) {
}
