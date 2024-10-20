package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.web.dto.ClientDTO;
import com.adi.smartcalendar.web.entity.Client;
import com.adi.smartcalendar.web.entity.Reservation;
import com.adi.smartcalendar.web.repository.ClientRepository;
import com.adi.smartcalendar.web.repository.ReservationRepository;
import com.adi.smartcalendar.web.service.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ReservationRepository reservationRepository;


    @Override
    public void createClient( ClientDTO clientDTO) {
        save(mapClientDTOToEntity(clientDTO));
    }

    @Override
    public void save( Client client) {
        clientRepository.save(client);
    }

    @Override
    public void modifyClient(Long clientId, ClientDTO clientDTO) {
       Client client = getClientById(clientId);

       client.setName(clientDTO.getName()==null ? client.getName() : clientDTO.getName());

       clientRepository.save(client);
    }

    @Override
    public void deleteClient(Long clientId) {
     Client clientToDelete = getClientById(clientId);

     Set<Reservation> reservationSet = reservationRepository.findByClientId(clientId);

     for(Reservation reservation : reservationSet){
         reservation.setClient(null);
         clientRepository.delete(clientToDelete);
         reservationRepository.save(reservation);
     }
    }


    @Override
    public ClientDTO getClientDTOById(Long clientId) {
        ClientDTO clientDTO = new ClientDTO();

        Client client = getClientById(clientId);

        clientDTO.setId(client.getId());
        clientDTO.setName(client.getName());

        return clientDTO;
    }

    @Override
    public Client getClientById(Long clientId) {
        return clientRepository.findById(clientId).orElseThrow(()-> new appException(HttpStatus.BAD_REQUEST,"CLIENT "+ ErrorCodeList.NF404));
    }

    @Override
    public List<ClientDTO> getAllClient() {
        return clientRepository.findAll().stream().map(this::mapEntityToClientDTO).toList();
    }

    private Client mapClientDTOToEntity(ClientDTO clientDTO) {
        Client client = new Client();
        client.setId(clientDTO.getId());
        client.setName(clientDTO.getName());
        return client;
    }

    private ClientDTO mapEntityToClientDTO(Client client) {
        ClientDTO clientDTO = new ClientDTO();

        clientDTO.setId(client.getId());

        clientDTO.setName(client.getName());

        return clientDTO;
    }
}
