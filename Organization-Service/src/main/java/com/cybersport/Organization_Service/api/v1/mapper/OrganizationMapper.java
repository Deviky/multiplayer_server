package com.cybersport.Organization_Service.api.v1.mapper;

import com.cybersport.Organization_Service.api.v1.dto.OrganizationDTO;
import com.cybersport.Organization_Service.entity.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    Organization DTOToEntity(OrganizationDTO organizationDTO);
    OrganizationDTO EntityToDTO(Organization organization);
}
