package com.ma_sante_assurance.client.mapper;

import com.ma_sante_assurance.client.dto.ClientResponseDTO;
import com.ma_sante_assurance.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", source = "id")
    ClientResponseDTO toDto(Client client);
}
