package com.cybersport.Organization_Service.service;

import com.cybersport.Organization_Service.api.v1.dto.OrganizationDTO;
import com.cybersport.Organization_Service.api.v1.mapper.OrganizationMapper;
import com.cybersport.Organization_Service.entity.Organization;
import com.cybersport.Organization_Service.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationDataService organizationDataService;

    @Autowired
    private OrganizationMapper organizationMapper;

    public Long addOrganization(OrganizationDTO organizationDTO){
        return organizationDataService.createOrganization(organizationMapper.DTOToEntity(organizationDTO));
    }

    public OrganizationDTO getOrganizationById(Long id){
        return organizationMapper.EntityToDTO(organizationDataService.findOrganizationById(id));
    }
}
