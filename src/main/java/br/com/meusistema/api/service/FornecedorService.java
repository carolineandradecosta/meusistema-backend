package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.FornecedorRequestDTO;
import br.com.meusistema.api.dtos.FornecedorResponseDTO;
import java.util.List;

public interface FornecedorService {
    FornecedorResponseDTO criarFornecedor(FornecedorRequestDTO dto);
    List<FornecedorResponseDTO> listarTodosFornecedores();
    FornecedorResponseDTO listarFornecedorPorId(Long id);
    FornecedorResponseDTO atualizarFornecedorPorId(Long id, FornecedorRequestDTO dto);
    void deletarFornecedorPorId(Long id);
}
