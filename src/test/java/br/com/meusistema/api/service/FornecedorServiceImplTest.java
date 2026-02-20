package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.EnderecoDTO;
import br.com.meusistema.api.dtos.FornecedorRequestDTO;
import br.com.meusistema.api.dtos.FornecedorResponseDTO;
import br.com.meusistema.api.enums.TipoFornecedorEnum;
import br.com.meusistema.api.mapper.EnderecoMapper;
import br.com.meusistema.api.mapper.FornecedorMapper;
import br.com.meusistema.api.model.Endereco;
import br.com.meusistema.api.model.Fornecedor;
import br.com.meusistema.api.repository.FornecedorRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceImplTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private FornecedorServiceImpl fornecedorService;

    private Fornecedor fornecedor;
    private Endereco endereco;
    private EnderecoDTO enderecoDTO;
    private FornecedorRequestDTO fornecedorRequestDTO;
    private FornecedorResponseDTO fornecedorResponseDTO;

    @BeforeEach
    void setup(){

        endereco = criarEndereco();
        enderecoDTO = criarEnderecoDTO();

        fornecedor = criarFornecedor(
                1L,
                "Fornecedor Teste",
                "fornecedorteste@gmail.com",
                "33822545000132"
        );

        fornecedorRequestDTO = criarFornecedorRequestDTO(
                "Fornecedor Teste",
                "fornecedorteste@gmail.com",
                "33822545000132"
        );

        fornecedorResponseDTO = criarFornecedorResponseDTO(
                1L,
                "Fornecedor Teste",
                "fornecedorteste@gmail.com",
                "33822545000132"
        );

    }

    @Test
    void deveCriarFornecedorComSucesso(){
        when(fornecedorMapper.toEntity(fornecedorRequestDTO)).thenReturn(fornecedor);
        //when(enderecoMapper.toEntity(fornecedorRequestDTO.endereco())).thenReturn(endereco);
        when(fornecedorRepository.save(fornecedor)).thenReturn(fornecedor);
        when(fornecedorMapper.toDTO(fornecedor)).thenReturn(fornecedorResponseDTO);

        FornecedorResponseDTO resposta = fornecedorService.criarFornecedor(fornecedorRequestDTO);

        assertNotNull(resposta);
        assertEquals(fornecedorResponseDTO, resposta);
        verify(fornecedorMapper).toEntity(fornecedorRequestDTO);
        //verify(enderecoMapper).toEntity(fornecedorRequestDTO.endereco());
        verify(fornecedorRepository).save(fornecedor);
        verify(fornecedorMapper).toDTO(fornecedor);
    }

    @Test
    void deveListarTodosOsFornecedores(){

        Fornecedor fornecedor2 = criarFornecedor(2L, "Fornecedor 2 da Lista", "fornecedor2@gmail.com", "24164212000172");
        Fornecedor fornecedor3 = criarFornecedor(3L, "Fornecedor 3 da Lista", "fornecedor3@gmail.com", "93307441000111");
        FornecedorResponseDTO fornecedorResponseDTO2 = criarFornecedorResponseDTO(2L, "Fornecedor 2 da Lista", "fornecedor2@gmail.com", "24164212000172");
        FornecedorResponseDTO fornecedorResponseDTO3 = criarFornecedorResponseDTO(3L, "Fornecedor 3 da Lista", "fornecedor3@gmail.com", "93307441000111");
        List<Fornecedor> fornecedores = List.of(fornecedor2, fornecedor3);

        when(fornecedorRepository.findAll()).thenReturn(fornecedores);
        when(fornecedorMapper.toDTO(fornecedor2)).thenReturn(fornecedorResponseDTO2);
        when(fornecedorMapper.toDTO(fornecedor3)).thenReturn(fornecedorResponseDTO3);
        List<FornecedorResponseDTO> resposta = fornecedorService.listarTodosFornecedores();

        assertNotNull(resposta);
        assertEquals(2, resposta.size());
        assertEquals(fornecedorResponseDTO2, resposta.get(0));
        assertEquals(fornecedorResponseDTO3, resposta.get(1));
        verify(fornecedorRepository).findAll();
        verify(fornecedorMapper, times(2)).toDTO(any(Fornecedor.class));

    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirFornecedores() {

        when(fornecedorRepository.findAll()).thenReturn(Collections.emptyList());

        List<FornecedorResponseDTO> resultado = fornecedorService.listarTodosFornecedores();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(fornecedorRepository).findAll();
        verifyNoInteractions(fornecedorMapper);
    }

    @Test
    void deveEncontrarFornecedorUtilizandoId(){

        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(fornecedorMapper.toDTO(fornecedor)).thenReturn(fornecedorResponseDTO);

        FornecedorResponseDTO resposta = fornecedorService.buscarFornecedorPorId(1L);

        assertNotNull(resposta);
        assertEquals(fornecedorResponseDTO, resposta);
        verify(fornecedorRepository).findById(1L);
        verify(fornecedorMapper).toDTO(fornecedor);
        verifyNoMoreInteractions(fornecedorRepository, fornecedorMapper);
    }

    @Test
    void deveLancarExcecaoQuandoFornecedorNaoExistirAoBuscarPorId(){
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
        assertThrows(ResponseStatusException.class,
                () -> fornecedorService.buscarFornecedorPorId(1L));

        assertEquals("Fornecedor não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(fornecedorRepository).findById(1L);
        verify(fornecedorMapper, never()).toDTO(any());
    }

    @Test
    void deveAtualizarFornecedorComSucesso(){
        Long id = 4l;

        Fornecedor fornecedorExistente = criarFornecedor(
                id,
                "Fornecedor Antigo",
                "antigo@email.com",
                "12345678000199"
        );

        FornecedorRequestDTO fornecedorRequestDTO = criarFornecedorRequestDTO(
                "Fornecedor Atualizado",
                "atualizado@email.com",
                "99887766000122"
        );

        Endereco enderecoAtualizado = criarEndereco();

        FornecedorResponseDTO fornecedorResponseDTO =
                criarFornecedorResponseDTO(
                        id,
                        "Fornecedor Atualizado",
                        "atualizado@email.com",
                        "99887766000122"
                );

        when(fornecedorRepository.findById(id)).thenReturn(Optional.of(fornecedorExistente));
        when(enderecoMapper.toEntity(fornecedorRequestDTO.endereco())).thenReturn(enderecoAtualizado);
        when(fornecedorRepository.save(fornecedorExistente)).thenReturn(fornecedorExistente);
        when(fornecedorMapper.toDTO(fornecedorExistente)).thenReturn(fornecedorResponseDTO);


        FornecedorResponseDTO resposta = fornecedorService.atualizarFornecedor(id, fornecedorRequestDTO);

        assertNotNull(resposta);
        assertEquals(fornecedorResponseDTO, resposta);

        verify(fornecedorRepository).findById(id);
        verify(enderecoMapper).toEntity(fornecedorRequestDTO.endereco());
        verify(fornecedorRepository).save(fornecedorExistente);
        verify(fornecedorMapper).toDTO(fornecedorExistente);

    }

    @Test
    void deveLancarExcecaoQuandoFornecedorNaoExistirAoAtualizarPorId(){
        Long id = 5L;

        FornecedorRequestDTO fornecedorRequestDTO = criarFornecedorRequestDTO(
                "Fornecedor Atualizado",
                "atualizado@email.com",
                "99887766000122"
        );

        when(fornecedorRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> fornecedorService.atualizarFornecedor(id, fornecedorRequestDTO));

        assertEquals("Fornecedor não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(fornecedorRepository).findById(id);
        verify(enderecoMapper, never()).toEntity(any());
        verify(fornecedorRepository, never()).save(any());
        verify(fornecedorMapper, never()).toDTO(any());

    }

    @Test
    void deveLancarExcecaoQuandoFornecedorNaoExistirAoDeletar(){
        when(fornecedorRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception =
        assertThrows(ResponseStatusException.class,
                () -> fornecedorService.deletarFornecedor(1L));

        assertEquals("Fornecedor não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(fornecedorRepository).existsById(1L);
        verify(fornecedorRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveDeletarFornecedorComSucesso(){
        when(fornecedorRepository.existsById(1L)).thenReturn(true);

        fornecedorService.deletarFornecedor(1L);

        verify(fornecedorRepository).existsById(1L);
        verify(fornecedorRepository).deleteById(1L);
        verifyNoMoreInteractions(fornecedorRepository);
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

    private Fornecedor criarFornecedor(Long id, String nome, String email, String cnpj) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(id);
        fornecedor.setNomeFantasia(nome);
        fornecedor.setEmail(email);
        fornecedor.setCnpj(cnpj);
        fornecedor.setTipoFornecedor(TipoFornecedorEnum.PREMIUM);
        fornecedor.setEndereco(criarEndereco());
        return fornecedor;
    }

    private FornecedorRequestDTO criarFornecedorRequestDTO(String nome, String email, String cnpj) {
        return new FornecedorRequestDTO(
                nome,
                email,
                cnpj,
                TipoFornecedorEnum.PREMIUM,
                criarEnderecoDTO()
        );
    }

    private FornecedorResponseDTO criarFornecedorResponseDTO(Long id, String nome, String email, String cnpj) {
        return new FornecedorResponseDTO(
                id,
                nome,
                email,
                cnpj,
                TipoFornecedorEnum.PREMIUM,
                criarEnderecoDTO()
        );
    }

}
