package com.cybersport.Organization_Service.api.v1.controller;

import com.cybersport.Organization_Service.api.v1.dto.OrganizationDTO;
import com.cybersport.Organization_Service.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDTO> findOrganizationId(@PathVariable Long id){
        OrganizationDTO organization = organizationService.getOrganizationById(id);
        if (organization == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(organization);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createOrganization(@RequestBody OrganizationDTO organization){
        Long id = organizationService.addOrganization(organization);
        if (id == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }


}
