package com.adi.smartcalendar.web.service.serviceImpl;

import com.axcent.DTO.OfficeDTO;
import com.axcent.entity.Office;
import com.axcent.entity.Reservation;
import com.axcent.exception.EntityErrorCodeList;
import com.axcent.repository.OfficeRepository;
import com.axcent.repository.ReservationRepository;
import com.axcent.security.exception.ErrorCodeList;
import com.axcent.security.exception.SmartAxcentException;
import com.axcent.service.OfficeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final ReservationRepository reservationRepository;

    //VOID RETURNS
    @Override
    public void createOffice(OfficeDTO officeDTO) {

        Office officeFound = getOfficeByAddress(officeDTO.getAddress());

        if (officeFound != null ){
            throw new SmartAxcentException(HttpStatus.BAD_REQUEST,"OFFICE "+EntityErrorCodeList.ALREADY_EXISTS);
        }


        Office office = mapOfficeDTOToEntity(officeDTO);

            save(office);
    }

    @Override
    public void save(Office office) {

        officeRepository.save(office);
    }
    @Transactional
    @Override
    public void deleteOffice(Long id) {

        Office officeToDelete = getOfficeById(id)
                .orElseThrow(()->  new SmartAxcentException(HttpStatus.BAD_REQUEST,"OFFICE "+ErrorCodeList.NF404));

        Set<Reservation> reservationSet = reservationRepository.findByOfficeId(id);
        for(Reservation reservation : reservationSet){
            reservation.setOffice(null);
            officeRepository.delete(officeToDelete);
            reservationRepository.save(reservation);
        }

    }

    @Override
    public void modifyOffice(Long id, OfficeDTO oDTO) {
        Office office = getOfficeById(id).orElseThrow(()->   new SmartAxcentException(HttpStatus.BAD_REQUEST,"OFFICE "+ ErrorCodeList.NF404));

        setOfficePropertiesFromDTO(office, oDTO);

        officeRepository.save(office);

    }

    //OFFICE RETURNS
    @Override
    public Optional<Office> getOfficeById(Long id) {

        return officeRepository.findById(id);
    }

    @Override
    public Office getOfficeByAddress(String address) {
        return officeRepository.findByAddress(address).orElse( null );
    }

    //OFFICEDTO RETURNS
    @Override
    public OfficeDTO getOfficeDTOById(Long id) {
        Office office = getOfficeById(id).orElseThrow(()-> new SmartAxcentException(HttpStatus.BAD_REQUEST,"OFFICE "+ ErrorCodeList.NF404));

        return mapOfficeToDTO(office);
    }

    @Override
    public List<OfficeDTO> getAllOffices() {

        List<Office> officeList = officeRepository.findAll();

        return officeList.stream().map(this::mapOfficeToDTO).toList();
    }



    @Override
    public Office mapOfficeDTOToEntity(OfficeDTO officeDTO) {
        Office office = new Office();

        office.setId(officeDTO.getId());

        if(officeDTO.getName() != null) {
            office.setName(officeDTO.getName());
        }
        if(officeDTO.getAddress() != null) {
            office.setAddress(officeDTO.getAddress());
        }
        if(officeDTO.getWorkstations() != null) {
            office.setWorkstations(officeDTO.getWorkstations());
        }
        return office;
    }

    private OfficeDTO mapOfficeToDTO(Office office) {
        OfficeDTO oDTO = new OfficeDTO();

        oDTO.setId(office.getId());

        oDTO.setName(office.getName());

        oDTO.setAddress(office.getAddress());

        oDTO.setWorkstations(office.getWorkstations());

        return oDTO;
    }

    private void setOfficePropertiesFromDTO(Office office, OfficeDTO oDTO) {

        office.setId(oDTO.getId() == null ? office.getId() : oDTO.getId());

        office.setName(oDTO.getName() == null ? office.getName() : oDTO.getName());

        office.setAddress(oDTO.getAddress() == null ? office.getAddress() : oDTO.getAddress());

        office.setWorkstations(oDTO.getWorkstations() == null ? office.getWorkstations() : oDTO.getWorkstations());

    }
}
