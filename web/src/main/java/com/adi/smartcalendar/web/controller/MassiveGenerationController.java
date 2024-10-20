package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.MassiveGenerationDTO;
import com.adi.smartcalendar.web.service.service.MassiveGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/massive_generation")
public class MassiveGenerationController {

    private final MassiveGenerationService massiveGenerationService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<MassiveGenerationDTO> generateMass( @RequestBody MassiveGenerationDTO massDTO) {
     return new ResponseEntity<>(massiveGenerationService.generateMass(massDTO),HttpStatus.CREATED);

    }
}
