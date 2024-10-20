package com.adi.smartcalendar.web.service.service;


import com.adi.smartcalendar.web.dto.ClientDTO;
import com.adi.smartcalendar.web.entity.Client;

import java.util.List;

public interface ClientService {

    void createClient( ClientDTO clientDTO);

    void save( Client client);

    void modifyClient(Long clientId,ClientDTO clientDTO);

    void deleteClient(Long clientId);

    Client getClientById(Long clientId);

    List<ClientDTO> getAllClient();

    ClientDTO getClientDTOById(Long clientId);


}
