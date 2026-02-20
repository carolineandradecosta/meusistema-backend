package br.com.meusistema.api.service;

import br.com.meusistema.api.dtos.EnderecoDTO;
import br.com.meusistema.api.dtos.FornecedorResponseDTO;
import br.com.meusistema.api.dtos.ProdutoRequestDTO;
import br.com.meusistema.api.dtos.ProdutoResponseDTO;
import br.com.meusistema.api.enums.TipoFornecedorEnum;
import br.com.meusistema.api.mapper.ProdutoMapper;
import br.com.meusistema.api.model.Endereco;
import br.com.meusistema.api.model.Fornecedor;
import br.com.meusistema.api.model.Produto;
import br.com.meusistema.api.repository.FornecedorRepository;
import br.com.meusistema.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceImplTest {

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;
    private Produto produto;
    private ProdutoRequestDTO produtoRequestDTO;
    private ProdutoResponseDTO produtoResponseDTO;
    private Fornecedor fornecedor;

    @BeforeEach
    void setup(){

        produto = criarProduto(1L, "Produto Teste", "450.61" , "Descrição do Produto Teste", 12);
        produtoRequestDTO = criarProdutoRequestDTO("Produto Teste", "450.61", "Descrição do Produto Teste", 12, 1L );
        produtoResponseDTO = criarProdutoResponseDTO(1L,"Produto Teste", "450.61", "Descrição do Produto Teste", 12);

        fornecedor = criarFornecedor();
    }

    @Test
    void deveCriarProdutoComSucesso() {
        when(produtoMapper.toEntity(produtoRequestDTO)).thenReturn(produto);
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.save(produto)).thenReturn(produto);
        when(produtoMapper.toDTO(produto)).thenReturn(produtoResponseDTO);

        ProdutoResponseDTO resposta = produtoService.criarProduto(produtoRequestDTO);

        assertNotNull(resposta);
        assertEquals(produtoResponseDTO, resposta);
        assertEquals(1L, resposta.fornecedor().id());

        verify(fornecedorRepository).findById(1L);
        verify(produtoMapper).toEntity(produtoRequestDTO);
        verify(produtoRepository).save(produto);
        verify(produtoMapper).toDTO(produto);
    }

    @Test
    void deveLancarExcecaoQuandoFornecedorNaoExistirAoCriarProduto() {
        when(produtoMapper.toEntity(produtoRequestDTO)).thenReturn(produto);
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
        assertThrows(ResponseStatusException.class,
                () -> produtoService.criarProduto(produtoRequestDTO));

        assertEquals("Fornecedor não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(fornecedorRepository).findById(1L);
        verify(produtoRepository, never()).save(any());
        verify(produtoMapper, never()).toDTO(any());

    }

    @Test
    void deveListarTodosOsProdutos(){

        Produto produto2 = criarProduto(2L, "Produto 2", "15.04" , "Descrição do Produto 1", 7);
        Produto produto3 = criarProduto(3L, "Produto 3", "98.16" , "Descrição do Produto 1", 16);
        ProdutoResponseDTO produtoResponseDTO2 = criarProdutoResponseDTO(2L, "Produto 2", "15.04" , "Descrição do Produto 1", 7);
        ProdutoResponseDTO produtoResponseDTO3 = criarProdutoResponseDTO(3L, "Produto 3", "98.16" , "Descrição do Produto 1", 16);
        List<Produto> produtos = List.of(produto2, produto3);

        when(produtoRepository.findAll()).thenReturn(produtos);
        when(produtoMapper.toDTO(produto2)).thenReturn(produtoResponseDTO2);
        when(produtoMapper.toDTO(produto3)).thenReturn(produtoResponseDTO3);
        List<ProdutoResponseDTO> resposta = produtoService.listarTodosProdutos();

        assertNotNull(resposta);
        assertEquals(2, resposta.size());
        assertEquals(produtoResponseDTO2, resposta.get(0));
        assertEquals(produtoResponseDTO3, resposta.get(1));
        verify(produtoRepository).findAll();
        verify(produtoMapper, times(2)).toDTO(any(Produto.class));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirProdutos() {

        when(produtoRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProdutoResponseDTO> resultado = produtoService.listarTodosProdutos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository).findAll();
        verifyNoInteractions(produtoMapper);
    }

    @Test
    void deveEncontrarProdutoUtilizandoId(){
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoMapper.toDTO(produto)).thenReturn(produtoResponseDTO);

        ProdutoResponseDTO resposta = produtoService.buscarProdutoPorId(1L);

        assertNotNull(resposta);
        assertEquals(produtoResponseDTO, resposta);
        verify(produtoRepository).findById(1L);
        verify(produtoMapper).toDTO(produto);
        verifyNoMoreInteractions(produtoRepository, produtoMapper);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistirAoBuscarPorId(){
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> produtoService.buscarProdutoPorId(1L));

        assertEquals("Produto não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(produtoRepository).findById(1L);
        verify(produtoMapper, never()).toDTO(any());
    }

    @Test
    void deveAtualizarFornecedorComSucesso(){
        Long id = 4l;

        Produto produtoExistente = criarProduto(id, "Produto Existente", "15.04" , "Descrição do Produto Antigo", 7);
        ProdutoRequestDTO produtoRequestDTO = criarProdutoRequestDTO("Produto Atualizado", "16.04", "Descrição do Produto Atualizado", 10, 1L );
        ProdutoResponseDTO produtoResponseDTO = criarProdutoResponseDTO(id, "Produto Atualizado", "16.04", "Descrição do Produto Atualizado", 10);

        when(produtoRepository.findById(id)).thenReturn(Optional.of(produtoExistente));
        when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.save(produtoExistente)).thenReturn(produtoExistente);
        when(produtoMapper.toDTO(produtoExistente)).thenReturn(produtoResponseDTO);

        ProdutoResponseDTO resposta = produtoService.atualizarProduto(id, produtoRequestDTO);

        assertNotNull(resposta);
        assertEquals(produtoResponseDTO, resposta);
        assertEquals(1L, resposta.fornecedor().id());
        verify(produtoRepository).findById(id);
        verify(produtoRepository).save(produtoExistente);
        verify(produtoMapper).toDTO(produtoExistente);

    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistirAoAtualizarPorId(){
        Long id = 5L;

        ProdutoRequestDTO produtoRequestDTO = criarProdutoRequestDTO("Produto Atualizado", "16.04", "Descrição do Produto Atualizado", 10, 1L );

        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> produtoService.atualizarProduto(id, produtoRequestDTO));

        assertEquals("Produto não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(produtoRepository).findById(id);
        verify(produtoRepository, never()).save(any());
        verify(produtoMapper, never()).toDTO(any());

    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistirAoDeletar(){
        when(produtoRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> produtoService.deletarProduto(1L));

        assertEquals("Produto não encontrado", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(produtoRepository).existsById(1L);
        verify(produtoRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveDeletarProdutoComSucesso(){
        when(produtoRepository.existsById(1L)).thenReturn(true);

        produtoService.deletarProduto(1L);

        verify(produtoRepository).existsById(1L);
        verify(produtoRepository).deleteById(1L);
        verifyNoMoreInteractions(produtoRepository);
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

    private Fornecedor criarFornecedor() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        fornecedor.setNomeFantasia("Fornecedor Teste");
        fornecedor.setEmail("fornecedorteste@gmail.com");
        fornecedor.setCnpj("33822545000132");
        fornecedor.setTipoFornecedor(TipoFornecedorEnum.PREMIUM);
        fornecedor.setEndereco(criarEndereco());
        return fornecedor;
    }

    private FornecedorResponseDTO criarFornecedorResponseDTO() {
        return new FornecedorResponseDTO(
                1L,
                "Fornecedor Teste",
                "fornecedorteste@gmail.com",
                "33822545000132",
                TipoFornecedorEnum.PREMIUM,
                criarEnderecoDTO()
        );
    }

    private Produto criarProduto(Long id, String nome, String preco, String descricao, Integer quantidadeEstoque) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setPreco(new BigDecimal(preco));
        produto.setDescricao(descricao);
        produto.setQuantidadeEstoque(quantidadeEstoque);
        produto.setFornecedor(criarFornecedor());
        return produto;
    }

    private ProdutoRequestDTO criarProdutoRequestDTO(String nome, String preco, String descricao, Integer quantidadeEstoque, Long fornecedor) {
        return new ProdutoRequestDTO(
                nome,
                new BigDecimal(preco),
                descricao,
                quantidadeEstoque,
                fornecedor
        );
    }

    private ProdutoResponseDTO criarProdutoResponseDTO(Long id, String nome, String preco, String descricao, Integer quantidadeEstoque) {
        return new ProdutoResponseDTO(
                id,
                nome,
                new BigDecimal(preco),
                descricao,
                quantidadeEstoque,
                criarFornecedorResponseDTO()
        );

    }














}
