package br.com.meusistema.api.service;


import br.com.meusistema.api.dtos.ClienteRequestDTO;
import br.com.meusistema.api.dtos.ClienteResponseDTO;
import java.util.List;

public interface ClienteService {
    ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO);
    List<ClienteResponseDTO> listarTodosClientes();
    ClienteResponseDTO buscarClientePeloId(Long id);
    ClienteResponseDTO atualizarClientePeloId(Long id, ClienteRequestDTO clienteRequestDTO);
    void deletarClientePeloId(Long id);
}
