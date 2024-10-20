package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.web.dto.ClientDTO;
import com.adi.smartcalendar.web.service.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CLIENT_CREATE') ")
    public ResponseEntity<Void> createEmployee(@Valid @RequestBody ClientDTO clientDTO) {
        clientService.createClient(clientDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('CLIENT_UPDATE')")
    public ResponseEntity<Void> modifyClient(@PathVariable("id") Long clientId,
                                             @Valid  @RequestBody ClientDTO clientDTO) {
        clientService.modifyClient(clientId, clientDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('CLIENT_DELETE')")
    public ResponseEntity<Void> deleteClient(@PathVariable("id") Long clientId){
        clientService.deleteClient(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_READ')")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable("id") Long clientId) {

        return new ResponseEntity<>(clientService.getClientDTOById(clientId),HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClientDTO>> getAllClient(){
        return new ResponseEntity<List<ClientDTO>>(clientService.getAllClient(),HttpStatus.OK);
    }

}
