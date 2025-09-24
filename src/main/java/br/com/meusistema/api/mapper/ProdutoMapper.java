package br.com.meusistema.api.mapper;

import br.com.meusistema.api.dtos.ProdutoRequestDTO;
import br.com.meusistema.api.dtos.ProdutoResponseDTO;
import br.com.meusistema.api.model.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "fornecedor", ignore = true)
    Produto toEntity(ProdutoRequestDTO produtoRequestDTO);

    ProdutoResponseDTO toDTO(Produto produto);

}
