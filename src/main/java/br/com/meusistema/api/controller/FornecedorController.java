package br.com.meusistema.api.controller;

import br.com.meusistema.api.dtos.FornecedorRequestDTO;
import br.com.meusistema.api.dtos.FornecedorResponseDTO;
import br.com.meusistema.api.service.FornecedorServiceImp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorServiceImp fornecedorServiceImp;

    @PostMapping
    public ResponseEntity<FornecedorResponseDTO> criarFornecedor(@Valid @RequestBody FornecedorRequestDTO dto) {
        FornecedorResponseDTO fornecedorCriado = fornecedorServiceImp.criarFornecedor(dto);
        return ResponseEntity.status(201).body(fornecedorCriado);
    }

    @GetMapping
    public ResponseEntity<List<FornecedorResponseDTO>> listarTodosFornecedores() {
        return ResponseEntity.ok(fornecedorServiceImp.listarTodosFornecedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> listarFornecedorPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorServiceImp.listarFornecedorPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponseDTO> atualizarFornecedorPorId
            (@PathVariable Long id,
             @Valid @RequestBody FornecedorRequestDTO dto
            ) {
        return ResponseEntity.ok(fornecedorServiceImp.atualizarFornecedorPorId(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFornecedorPorId(@PathVariable Long id) {
        fornecedorServiceImp.deletarFornecedorPorId(id);
        return ResponseEntity.noContent().build();
    }

}
