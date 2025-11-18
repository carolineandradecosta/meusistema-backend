package br.com.meusistema.api.controller;

import br.com.meusistema.api.dtos.ClienteRequestDTO;
import br.com.meusistema.api.dtos.ClienteResponseDTO;
import br.com.meusistema.api.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ClienteResponseDTO> criarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO){
        ClienteResponseDTO clienteCriado = clienteService.criarCliente(clienteRequestDTO);
        return ResponseEntity.status(201).body(clienteCriado);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ClienteResponseDTO>> listarTodosClientes (){
        return ResponseEntity.ok(clienteService.listarTodosClientes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ClienteResponseDTO> buscarClientePeloId (@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarClientePeloId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ClienteResponseDTO> atualizarClientePeloId
            (@PathVariable Long id,
             @Valid @RequestBody ClienteRequestDTO clienteRequestDTO){
        return ResponseEntity.ok(clienteService.atualizarClientePeloId(id, clienteRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarClientePeloId(@PathVariable Long id){
        clienteService.deletarClientePeloId(id);
        return ResponseEntity.noContent().build();
    }

}
