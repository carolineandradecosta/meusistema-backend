package br.com.meusistema.api.dtos;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        String descricao,
        Integer quantidadeEstoque,
        FornecedorResponseDTO fornecedor
) {
}
