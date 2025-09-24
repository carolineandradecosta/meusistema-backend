package br.com.meusistema.api.mapper;

import br.com.meusistema.api.dtos.FornecedorRequestDTO;
import br.com.meusistema.api.dtos.FornecedorResponseDTO;
import br.com.meusistema.api.model.Fornecedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FornecedorMapper {

    @Mapping(target = "id", ignore = true)
    Fornecedor toEntity(FornecedorRequestDTO fornecedorRequestDTO);

    FornecedorResponseDTO toDTO(Fornecedor fornecedor);
}
