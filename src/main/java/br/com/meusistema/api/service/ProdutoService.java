package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.ProdutoRequestDTO;
import br.com.meusistema.api.dtos.ProdutoResponseDTO;

import java.util.List;

public interface ProdutoService {
    ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO);
    List<ProdutoResponseDTO> listarTodos();
    ProdutoResponseDTO buscarPorId(Long id);
    ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO produtoRequestDTO);
    void deletar(Long id);
}
