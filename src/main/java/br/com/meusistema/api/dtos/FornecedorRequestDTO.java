package br.com.meusistema.api.dtos;

import br.com.meusistema.api.enums.TipoFornecedorEnum;
import br.com.meusistema.api.model.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record FornecedorRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nomeFantasia,

        @Email(message = "Email inválido")
        String email,

        @CNPJ(message = "CNPJ inválido")
        String cnpj,

        @NotNull(message = "Tipo do Fornecedor é obrigatório")
        TipoFornecedorEnum tipoFornecedor,

        @Valid
        EnderecoDTO endereco
) {
}
