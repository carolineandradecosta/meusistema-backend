package br.com.meusistema.api.dtos;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        EnderecoDTO endereco
) {
}
