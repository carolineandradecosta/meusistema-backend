package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.ProdutoRequestDTO;
import br.com.meusistema.api.dtos.ProdutoResponseDTO;

import java.util.List;

public interface ProdutoService {
    ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO);
    List<ProdutoResponseDTO> listarTodosProdutos();
    ProdutoResponseDTO buscarProdutoPorId(Long id);
    ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO);
    void deletarProduto(Long id);
}
