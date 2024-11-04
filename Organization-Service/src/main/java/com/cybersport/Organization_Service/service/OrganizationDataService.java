package com.cybersport.Organization_Service.service;

import com.cybersport.Organization_Service.entity.Organization;
import com.cybersport.Organization_Service.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrganizationDataService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public Long createOrganization(Organization organization){
        if (findOrganizationByName(organization.getName()) != null)
            return null;

        organizationRepository.save(organization);
        return organization.getId();
    }

    private Organization findOrganizationByName(String name) {
        return organizationRepository.findByName(name);
    }

    @Cacheable("organization")
    public Organization findOrganizationById(Long id){
        return organizationRepository.findById(id).orElse(null);
    }

}
