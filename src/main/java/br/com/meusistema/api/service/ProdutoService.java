package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.ProdutoRequestDTO;
import br.com.meusistema.api.dtos.ProdutoResponseDTO;

public interface ProdutoService {
    ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO);
}
