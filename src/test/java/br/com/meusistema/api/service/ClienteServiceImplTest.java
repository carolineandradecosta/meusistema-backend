package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.ClienteRequestDTO;
import br.com.meusistema.api.dtos.ClienteResponseDTO;
import br.com.meusistema.api.dtos.EnderecoDTO;
import br.com.meusistema.api.mapper.ClienteMapper;
import br.com.meusistema.api.mapper.EnderecoMapper;
import br.com.meusistema.api.model.Cliente;
import br.com.meusistema.api.model.Endereco;
import br.com.meusistema.api.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private EnderecoMapper enderecoMapper;


    @InjectMocks
    private ClienteServiceImpl clienteService;
    private Cliente cliente;
    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;
    private Endereco endereco;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setup(){
        endereco = criarEndereco();
        enderecoDTO = criarEnderecoDTO();
        cliente = criarCliente(1L, "Cliente Teste", "76150583767", "clienteteste@gmail.com", "83999886645");
        clienteRequestDTO = criarClienteRequestDTO("Cliente Teste", "76150583767", "clienteteste@gmail.com", "83999883311");
        clienteResponseDTO = criarClienteResponseDTO(1L,"Cliente Teste", "76150583767", "clienteteste@gmail.com", "83999883311");
    }

    @Test
    void deveCriarClienteComSucesso(){
        when(clienteMapper.toEntity(clienteRequestDTO)).thenReturn(cliente);
        when(enderecoMapper.toEntity(clienteRequestDTO.endereco())).thenReturn(endereco);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteResponseDTO);

        ClienteResponseDTO resposta = clienteService.criarCliente(clienteRequestDTO);

        assertNotNull(resposta);
        assertEquals(clienteResponseDTO, resposta);
        verify(clienteMapper).toEntity(clienteRequestDTO);
        verify(enderecoMapper).toEntity(clienteRequestDTO.endereco());
        verify(clienteRepository).save(cliente);
        verify(clienteMapper).toDTO(cliente);
    }

    @Test
    void deveListarTodosOsClientes(){
        Cliente cliente2 = criarCliente(2L, "Cliente 2", "12138660060", "cliente2@gmail.com", "83988774455");
        Cliente cliente3 = criarCliente(3L, "Cliente 3", "66552732775", "cliente3@gmail.com", "83999884455");
        ClienteResponseDTO clienteResponseDTO2 = criarClienteResponseDTO(2L,"Cliente 2", "12138660060", "cliente2@gmail.com", "83988774455");
        ClienteResponseDTO clienteResponseDTO3 = criarClienteResponseDTO(3L,"Cliente 3", "7615066552732775583767", "cliente3@gmail.com", "83999884455");
        List<Cliente> clientes = List.of(cliente2, cliente3);

        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteMapper.toDTO(cliente2)).thenReturn(clienteResponseDTO2);
        when(clienteMapper.toDTO(cliente3)).thenReturn(clienteResponseDTO3);
        List<ClienteResponseDTO> resposta = clienteService.listarTodosClientes();

        assertNotNull(resposta);
        assertEquals(2, resposta.size());
        assertEquals(clienteResponseDTO2, resposta.get(0));
        assertEquals(clienteResponseDTO3, resposta.get(1));
        verify(clienteRepository).findAll();
        verify(clienteMapper, times(2)).toDTO(any(Cliente.class));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirClientes() {
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        List<ClienteResponseDTO> resultado = clienteService.listarTodosClientes();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(clienteRepository).findAll();
        verifyNoInteractions(clienteMapper);
    }

    @Test
    void deveEncontrarClienteUtilizandoId(){
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(clienteResponseDTO);
        ClienteResponseDTO resposta = clienteService.buscarClientePorId(1L);

        assertNotNull(resposta);
        assertEquals(clienteResponseDTO, resposta);

        verify(clienteRepository).findById(1L);
        verify(clienteMapper).toDTO(cliente);
        verifyNoMoreInteractions(clienteRepository, clienteMapper);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExistirAoBuscarPorId(){
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> clienteService.buscarClientePorId(1L));

        assertEquals("Cliente não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(clienteRepository).findById(1L);
        verify(clienteMapper, never()).toDTO(any());
    }

    @Test
    void deveAtualizarClienteComSucesso(){
        Long id = 4l;
        Cliente clienteExistente = criarCliente(id, "Cliente Antigo", "93217574575", "clienteantigo@gmail.com", "83987554125");
        ClienteRequestDTO clienteRequestDTO = criarClienteRequestDTO("Cliente Atualizado","93217574575",  "clienteatualizado@gmail.com", "83987554125");
        Endereco enderecoAtualizado = criarEndereco();
        ClienteResponseDTO clienteResponseDTO = criarClienteResponseDTO(id,"Cliente Atualizado", "93217574575", "clienteatualizado@gmail.com", "83987554125");
        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(enderecoMapper.toEntity(clienteRequestDTO.endereco())).thenReturn(enderecoAtualizado);
        when(clienteRepository.save(clienteExistente)).thenReturn(clienteExistente);
        when(clienteMapper.toDTO(clienteExistente)).thenReturn(clienteResponseDTO);
        ClienteResponseDTO resposta = clienteService.atualizarCliente(id, clienteRequestDTO);

        assertNotNull(resposta);
        assertEquals(clienteResponseDTO, resposta);

        verify(clienteRepository).findById(id);
        verify(enderecoMapper).toEntity(clienteRequestDTO.endereco());
        verify(clienteRepository).save(clienteExistente);
        verify(clienteMapper).toDTO(clienteExistente);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExistirAoAtualizarPorId(){
        Long id = 5L;
        ClienteRequestDTO clienteRequestDTO = criarClienteRequestDTO("Cliente Atualizado","93217574575",  "clienteatualizado@gmail.com", "83987554125");
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> clienteService.atualizarCliente(id, clienteRequestDTO));

        assertEquals("Cliente não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(clienteRepository).findById(id);
        verify(enderecoMapper, never()).toEntity(any());
        verify(clienteRepository, never()).save(any());
        verify(clienteMapper, never()).toDTO(any());
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExistirAoDeletar(){
        when(clienteRepository.existsById(1L)).thenReturn(false);
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> clienteService.deletarCliente(1L));

        assertEquals("Cliente não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(clienteRepository).existsById(1L);
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveDeletarClienteComSucesso(){
        when(clienteRepository.existsById(1L)).thenReturn(true);
        clienteService.deletarCliente(1L);

        verify(clienteRepository).existsById(1L);
        verify(clienteRepository).deleteById(1L);
        verifyNoMoreInteractions(clienteRepository);
    }

        private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setLogradouro("Rua Antônio Vieira da Rocha");
        endereco.setNumero("336");
        endereco.setComplemento("casa");
        endereco.setBairro("bodocongó");
        endereco.setCidade("Campina Grande");
        endereco.setEstado("Paraíba");
        endereco.setPais("Brasil");
        endereco.setCep("58430460");
        return endereco;
    }

    private EnderecoDTO criarEnderecoDTO() {
        return new EnderecoDTO(
                "Rua Antônio Vieira da Rocha",
                "336",
                "casa",
                "bodocongó",
                "Campina Grande",
                "Paraíba",
                "Brasil",
                "58430460"
        );
    }

    private Cliente criarCliente(Long id, String nome, String cpf, String email, String telefone) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome(nome);
        cliente.setCpf(cpf);
        cliente.setEmail(email);
        cliente.setTelefone("83998987887");
        cliente.setEndereco(criarEndereco());
        return cliente;
    }

    private ClienteRequestDTO criarClienteRequestDTO(String nome, String cpf, String email, String telefone) {
        return new ClienteRequestDTO(
                nome,
                email,
                cpf,
                telefone,
                criarEnderecoDTO()
        );
    }

    private ClienteResponseDTO criarClienteResponseDTO(Long id, String nome, String cpf, String email, String telefone) {
        return new ClienteResponseDTO(
                id,
                nome,
                email,
                cpf,
                telefone,
                criarEnderecoDTO()
        );
    }

}
