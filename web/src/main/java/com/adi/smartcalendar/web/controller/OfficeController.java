package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.OfficeDTO;
import com.adi.smartcalendar.web.service.service.OfficeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/office")
public class OfficeController {

    private final OfficeService officeService;

    @Autowired
    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('OFFICE_CREATE')")
    public ResponseEntity<Void> createOffice(@Valid @RequestBody OfficeDTO oDTO) {
        officeService.createOffice(oDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('OFFICE_UPDATE')")
    public ResponseEntity<Void> modifyOffice(@PathVariable("id") Long id,
                                             @Valid @RequestBody OfficeDTO oDTO) {
        officeService.modifyOffice(id, oDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('OFFICE_DELETE')")
    public ResponseEntity<Void> deleteOffice(@PathVariable("id") Long id) {
        officeService.deleteOffice(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('OFFICE_READ')")
    public ResponseEntity<OfficeDTO> getOfficeById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(officeService.getOfficeDTOById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<OfficeDTO>> getAllOffices(){
        return new ResponseEntity<>(officeService.getAllOffices(),HttpStatus.OK);
    }


}
