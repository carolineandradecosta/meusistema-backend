package br.com.meusistema.api.mapper;

import br.com.meusistema.api.dtos.EnderecoDTO;
import br.com.meusistema.api.model.Endereco;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    Endereco toEntity(EnderecoDTO enderecoDTO);

    EnderecoDTO toDTO(Endereco endereco);
}
