package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.dto.SignupDTO;
import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.security.service.UserService;
import com.adi.smartcalendar.web.dto.EmployeeDTO;
import com.adi.smartcalendar.web.dto.MassiveGenerationDTO;
import com.adi.smartcalendar.web.dto.UserEmployeeDTO;
import com.adi.smartcalendar.web.service.service.EmployeeService;
import com.adi.smartcalendar.web.service.service.MassiveGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MassiveGenerationServiceImpl implements MassiveGenerationService {

    private final UserService userService;
    private final EmployeeService employeeService;

    //METODO DI GENERAZIONE MASSIVA DEGLI USER E DEI DIPENDENTI ASSOCIATI
    @Override
    public MassiveGenerationDTO generateMass( MassiveGenerationDTO massiveGenerationDTO) {
        MassiveGenerationDTO notCreatedDTOs = new MassiveGenerationDTO();

        List<UserEmployeeDTO> userEmployeeDTOList = new ArrayList<>();

        //PER OGNI MDTO DELLA LISTA
        massiveGenerationDTO.getUserEmployeeDTOList().forEach(uDTO -> {
            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setEmail(uDTO.getEmail());
            signupDTO.setUsername(uDTO.getUsername());

            try {
                //todo: rimuovere il parametro booleano in fase di produzione
                UserDTO user = userService.createUser(signupDTO);
                EmployeeDTO employeeDTO = new EmployeeDTO();
                employeeDTO.setUserId(user.getId());
                employeeDTO.setName(uDTO.getName());
                employeeDTO.setSurname(uDTO.getSurname());
                employeeDTO.setProjectName(uDTO.getProjectName());
                employeeService.createEmployee(employeeDTO);


                //SE FALLISCE LA CREAZIONE AGGIUNGE UNO USEREMPLOYEEDTO ALLA LISTA CHE VERRA' AGGIUNTA AI NOTCREATED
            }catch (RuntimeException error){

                UserEmployeeDTO userEmployeeDTO = new UserEmployeeDTO();
                userEmployeeDTO.setName(uDTO.getName());
                userEmployeeDTO.setSurname(uDTO.getSurname());
                userEmployeeDTO.setEmail(uDTO.getEmail());
                userEmployeeDTO.setUsername(uDTO.getUsername());
                userEmployeeDTO.setProjectName(uDTO.getProjectName());
                userEmployeeDTO.setErrorMessage(error.getMessage());
                userEmployeeDTOList.add(userEmployeeDTO);
            }
        });
        notCreatedDTOs.setUserEmployeeDTOList(userEmployeeDTOList);
        return notCreatedDTOs;
    }
}
