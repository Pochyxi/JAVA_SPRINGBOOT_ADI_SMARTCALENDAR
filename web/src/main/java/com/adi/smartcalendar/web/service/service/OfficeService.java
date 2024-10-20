package com.adi.smartcalendar.web.service.service;


import com.adi.smartcalendar.web.dto.OfficeDTO;
import com.adi.smartcalendar.web.entity.Office;

import java.util.List;
import java.util.Optional;

public interface OfficeService {

    // VOID RETURNS

    void createOffice( OfficeDTO officeDTO);

    void save( Office office);

    void deleteOffice(Long id);

    void modifyOffice(Long id,OfficeDTO oDTO);

    //OFFICE RETURNS

    Optional<Office> getOfficeById(Long id);

    Office getOfficeByAddress(String address);

    Office mapOfficeDTOToEntity(OfficeDTO officeDTO);

    //DTO RETURNS

    OfficeDTO getOfficeDTOById(Long id);

    List<OfficeDTO> getAllOffices();

}
