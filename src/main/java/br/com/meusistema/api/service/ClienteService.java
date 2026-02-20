package br.com.meusistema.api.service;


import br.com.meusistema.api.dtos.ClienteRequestDTO;
import br.com.meusistema.api.dtos.ClienteResponseDTO;
import java.util.List;

public interface ClienteService {
    ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO);
    List<ClienteResponseDTO> listarTodosClientes();
    ClienteResponseDTO buscarClientePorId(Long id);
    ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO clienteRequestDTO);
    void deletarCliente(Long id);
}
