package br.com.meusistema.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nome,

        @CPF
        String cpf,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail é inválido")
        String email,

        @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve ter 10 ou 11 dígitos numéricos")
        String telefone,

        @Valid
        EnderecoDTO endereco

) {
}
