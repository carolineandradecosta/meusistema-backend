package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.ClienteRequestDTO;
import br.com.meusistema.api.dtos.ClienteResponseDTO;
import br.com.meusistema.api.mapper.ClienteMapper;
import br.com.meusistema.api.mapper.EnderecoMapper;
import br.com.meusistema.api.model.Cliente;
import br.com.meusistema.api.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final EnderecoMapper enderecoMapper;


    @Override
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = clienteMapper.toEntity(clienteRequestDTO);
        cliente.setEndereco(enderecoMapper.toEntity(clienteRequestDTO.endereco()));
        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Override
    public List<ClienteResponseDTO> listarTodosClientes() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper :: toDTO)
                .toList();
    }

    @Override
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente não encontrado"));
        return clienteMapper.toDTO(cliente);
    }

    @Transactional
    @Override
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO clienteRequestDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        cliente.setNome(clienteRequestDTO.nome());
        cliente.setCpf(clienteRequestDTO.cpf());
        cliente.setEmail(clienteRequestDTO.email());
        cliente.setTelefone(clienteRequestDTO.telefone());
        cliente.setEndereco(enderecoMapper.toEntity(clienteRequestDTO.endereco()));

        return clienteMapper.toDTO(clienteRepository.save(cliente));
    }

    @Transactional
    @Override
    public void deletarCliente(Long id) {
        if(!clienteRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
}
