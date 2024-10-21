package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.security.dto.ProfileDTO;
import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.security.dto.UserDTOInternal;
import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.security.service.UserService;
import com.adi.smartcalendar.web.dto.EmployeeDTO;
import com.adi.smartcalendar.web.entity.Employee;
import com.adi.smartcalendar.web.entity.Project;
import com.adi.smartcalendar.web.repository.EmployeeRepository;
import com.adi.smartcalendar.web.service.service.EmployeeService;
import com.adi.smartcalendar.web.service.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final ProjectService projectService;


    //VOID RETURNS


    // Questo metodo salva un dipendente nel database
    @Override
    public void save( Employee employee ) {
        employeeRepository.save( employee );
    }

    // Questo metodo crea un dipendente nel database
    @Override
    @Transactional
    public EmployeeDTO createEmployee( EmployeeDTO employeeDTO ) {
        // Recupero l'utente dal database in base all'id, se non esiste viene creato un nuovo utente
        Employee employeeFound = employeeRepository.findByUserId( employeeDTO.getUserId() )
                .orElseGet( Employee::new );

        // Se l'id del dipendente è diverso da null, vuol dire che esiste già un dipendente con quell'id
        // e quindi viene lanciata un'eccezione
        if( employeeFound.getUserId() != null ) {
            throw new appException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeList.ALREADY_EXISTS );
        }

        // Mappatura da DTO a entity
        employeeFound = mapToEntity( employeeDTO );

        if( employeeFound.getProject() == null ) {
            Project defaultProject = projectService.getProjectByName( "Pending" );

            employeeFound.setProject( defaultProject );
        }

        employeeFound.setEmployeeCode(  "AX" + employeeFound.getUserId() + new Random().nextInt( 100000 ) );

        return mapEmployeeToDTO( employeeRepository.save( employeeFound ) );
    }

    // Questo metodo sposta un dipendente da un progetto ad un altro
    @Override
    @Transactional
    public void moveEmployeeToProject( Long projectId, Long employeeId ) {
        Project project = projectService.getProjectById( projectId );

        Employee employee = employeeRepository.findByUserId( employeeId ).orElseThrow( () -> new appException(HttpStatus.BAD_REQUEST,"EMPLOYEE "+ErrorCodeList.NF404) );

        // Se stiamo spostando il dipendente sullo stesso progetto
        if( Objects.equals( employee.getProject().getName(), project.getName() ) ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.ALREADY_EXISTS );
        }

        Set<Employee> setEmpl = project.getEmployees();

        setEmpl.add( employee );

        project.setEmployees( setEmpl );

        Project projectSaved = projectService.save( project );

        employee.setProject( projectSaved );

        employeeRepository.save( employee );
    }

    // Questo metodo elimina un dipendente dal database
    @Transactional
    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findByUserId(id)
                .orElseThrow(() -> new appException(HttpStatus.BAD_REQUEST,"EMPLOYEE "+ ErrorCodeList.NF404));

        // Gestione della relazione bidirezionale
        Project project = employee.getProject();
        if (project != null) {
            project.getEmployees().remove(employee);
            projectService.save(project); // Salva il progetto aggiornato
            employee.setProject(null);
        }

        // Salviamo le modifiche all'employee
        employeeRepository.save(employee);

        // Procediamo con l'eliminazione del dipendente
        employeeRepository.delete(employee);
    }

    @Override
    public void removeBlockFromEmployee(Long employeeId) {
        Employee employee = getEmployeeById(employeeId);

        employee.setBlockedFrom(null);

        employee.setBlockedTo(null);

        save(employee);
    }


    // Questo metodo recupera un dipendente dal database in base al suo user id
    // Ritorna null se non viene trovato
    @Override
    public Employee getEmployeeByUserId( Long userId ) {
        Optional<Employee> employeeFound = employeeRepository.findByUserId( userId );

        return employeeFound.orElse( null );
    }




    // Questo metodo recupera un dipendente dal database in base al suo user id
    // Lancia un'eccezione se non viene trovato
    public Employee getEmployeeById( Long id ) {
        return employeeRepository.findByUserId( id )
                .orElseThrow( () -> new appException(HttpStatus.BAD_REQUEST,"EMPLOYEE "+ ErrorCodeList.NF404 ) );
    }


    //Metodo per mappare un dipendenteDTO a Dipendente
    public Employee mapToEntity( EmployeeDTO employeeDTO ) {
        Employee employee = new Employee();

        UserDTO user = userService.findById( employeeDTO.getUserId() );
        ProfileDTO profile = userService.getProfile( user.getId() );

        employee.setUserEmail( user.getEmail() );
        employee.setUserProfilePower( profile.getPower() );
        employee.setUserId( employeeDTO.getUserId() );
        employee.setEmployeeCode( employeeDTO.getEmployeeCode() );
        employee.setName( employeeDTO.getName() );
        employee.setSurname( employeeDTO.getSurname() );
        employee.setAssumptionDate( employeeDTO.getAssumptionDate() );
        employee.setBirthDate( employeeDTO.getBirthDate() );
        employee.setUnilavCode( employeeDTO.getUnilavCode() );
        employee.setBlockedFrom( employeeDTO.getBlockedFrom() );
        employee.setBlockedTo( employeeDTO.getBlockedTo() );

        Project projectAdded = projectService.getProjectByName( employeeDTO.getProjectName() );

        employee.setProject( projectAdded );

        employee.setUserId( user.getId() );


        return employee;
    }

    //DTO RETURNS

    //Questo metodo restituisce une dipendenteDTO specifico in base al suo id Utente

    @Override
    public EmployeeDTO getEmployeeDTOByUserId( Long userId ) {
        Employee employee = getEmployeeById( userId );
        return mapEmployeeToDTO( employee );
    }


    @Override
    public Page<Employee> findByUserProfilePowerGreaterThanEqual( int power, Pageable page ) {
        return employeeRepository.findByUserProfilePowerGreaterThanEqual( page, power );
    }

    @Override
    public PagedResponseDTO<UserDTO> getAllUsersByProjectName( String projectName, int pageNo, int pageSize, String sortBy, String sortDir ) {

        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();

        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );

        Page<Employee> employeePage = employeeRepository.findByProjectName( pageable, projectName );


        List<UserDTO> userList = mapEmployeeListToUserDTOList( employeePage.getContent() );


        PagedResponseDTO<UserDTO> userResponseDTO = new PagedResponseDTO<>();

        userResponseDTO.setContent( userList );

        userResponseDTO.setPageNo( employeePage.getNumber() );

        userResponseDTO.setPageSize( employeePage.getSize() );

        userResponseDTO.setTotalElements( employeePage.getTotalElements() );

        userResponseDTO.setTotalPages( employeePage.getTotalPages() );

        userResponseDTO.setLast( employeePage.isLast() );

        return userResponseDTO;

    }

    @Override
    public PagedResponseDTO<UserDTO> getAllUsersByProjectNameAndEmailContains( String projectName, String userEmail, int pageNo, int pageSize, String sortBy, String sortDir ) {

        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();

        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );


        Page<Employee> employeePage = employeeRepository.findByProjectNameAndUserEmailContains( pageable, projectName, userEmail );


        List<UserDTO> userList = mapEmployeeListToUserDTOList( employeePage.getContent() );



        PagedResponseDTO<UserDTO> userResponseDTO = new PagedResponseDTO<>();

        userResponseDTO.setContent( userList );

        userResponseDTO.setPageNo( employeePage.getNumber() );

        userResponseDTO.setPageSize( employeePage.getSize() );

        userResponseDTO.setTotalElements( employeePage.getTotalElements() );

        userResponseDTO.setTotalPages( employeePage.getTotalPages() );

        userResponseDTO.setLast( employeePage.isLast() );

        return userResponseDTO;
    }


    private List<UserDTO> mapEmployeeListToUserDTOList( List<Employee> employeeList ) {
        return employeeList.stream().map( (employee -> {
            Long userId = employee.getUserId();
            return userService.findById( userId );
        }) ).toList();
    }


    @Override
    public Set<EmployeeDTO> findAllEmployeesByProjectId( Long projectId ) {
        Set<Employee> employees = employeeRepository.findByProjectId( projectId );

        return employees.stream().map( this::mapEmployeeToDTO ).collect( Collectors.toSet() );

    }

    @Override
    public Set<EmployeeDTO> findAllEmployeesByProjectName( String projectName ) {
        Set<Employee> employees = employeeRepository.findByProjectName( projectName );
        return employees.stream().map( this::mapEmployeeToDTO ).collect( Collectors.toSet() );
    }


    @Override
    public PagedResponseDTO<EmployeeDTO> getAllEmployees( int pageNo, int pageSize, String sortBy, String sortDir ) {

        Sort sort = sortDir.equalsIgnoreCase( Sort.Direction.ASC.name() ) ? Sort.by( sortBy ).ascending()
                : Sort.by( sortBy ).descending();

        Pageable pageable = PageRequest.of( pageNo, pageSize, sort );

        UserDTOInternal user = userService.getUserByAuthentication();

        ProfileDTO profile = userService.getProfile( user.getId() );

        int powerOfUser = profile.getPower();

        Page<Employee> employeePageList = findByUserProfilePowerGreaterThanEqual( powerOfUser, pageable );

        List<Employee> employeeList = employeePageList.getContent();

        List<EmployeeDTO> eDTOList = employeeList.stream().map( this::mapEmployeeToDTO ).toList();

        return getPagedResponseDTOFromEmployeeDTO( eDTOList, employeePageList );
    }


    @Override
    public EmployeeDTO modifyEmployee( Long userId, EmployeeDTO employeeDTO ) {
        // Nel momento in cui si modifica un dipendente, se il from è null e il to è pieno, o viceversa, viene lanciata
        // un'eccezione
        if( (employeeDTO.getBlockedFrom() != null && employeeDTO.getBlockedTo() == null) ||
                (employeeDTO.getBlockedFrom() == null && employeeDTO.getBlockedTo() != null) ) {
            throw new appException( HttpStatus.BAD_REQUEST, "BLOCKED FROM/TO " + ErrorCodeList.CANNOT_BE_EMPTY );
        }

        // Nel momento in cui si modifica un dipendente, se il from è pieno e il to è pieno, ma il to è prima del from,
        // viene lanciata un'eccezione
        if( employeeDTO.getBlockedTo() != null && employeeDTO.getBlockedTo().isBefore( employeeDTO.getBlockedFrom() ) ) {
            throw new appException( HttpStatus.BAD_REQUEST, "BLOCKED TO " + ErrorCodeList.DATE_INVALID );
        }

        Employee employee = getEmployeeById( userId );

        setEmployeePropertiesFromDTO( userId, employeeDTO, employee );

        Employee employeeSaved = employeeRepository.save( employee );

        return mapEmployeeToDTO( employeeSaved );
    }


    //PRIVATE METHODS


    //Metodo che svolge la mappatura da entity a DTO
    public EmployeeDTO mapEmployeeToDTO( Employee employee ) {
        EmployeeDTO eDTO = new EmployeeDTO();

        eDTO.setEmployeeCode( employee.getEmployeeCode() );

        eDTO.setName( employee.getName() );

        eDTO.setSurname( employee.getSurname() );

        eDTO.setAssumptionDate( employee.getAssumptionDate() );

        eDTO.setBirthDate( employee.getBirthDate() );

        eDTO.setUnilavCode( employee.getUnilavCode() );

        eDTO.setBlockedFrom( employee.getBlockedFrom() );

        eDTO.setBlockedTo( employee.getBlockedTo() );

        eDTO.setUserId( employee.getUserId() );

        eDTO.setUserEmail( employee.getUserEmail() );

        eDTO.setUserProfilePower( employee.getUserProfilePower() );

        eDTO.setProjectName( employee.getProject() == null ? null : employee.getProject().getName() );

        return eDTO;

    }



    /*
     Questo metodo accetta un EmployeeDTO e un Employee e setta le proprietà di Employee in base alle informazioni
     ricavate dall'oggetto EmployeeDTO
     */
    private void setEmployeePropertiesFromDTO( Long userId, EmployeeDTO eDTO, Employee employee ) {
        UserDTO user = userService.findById( userId );

        if( (eDTO.getBlockedFrom() != null && eDTO.getBlockedTo() == null) ||
                (eDTO.getBlockedFrom() == null && eDTO.getBlockedTo() != null) ) {
            throw new appException( HttpStatus.BAD_REQUEST, "BLOCKED FROM/TO " + ErrorCodeList.CANNOT_BE_EMPTY );
        }
        if( eDTO.getBlockedFrom() != null ) {
            if( eDTO.getBlockedTo().isBefore( eDTO.getBlockedFrom() ) ) {
                throw new appException( HttpStatus.BAD_REQUEST, "BLOCKED TO " + ErrorCodeList.DATE_INVALID );
            }
        }

        employee.setEmployeeCode( eDTO.getEmployeeCode() == null ? employee.getEmployeeCode() : eDTO.getEmployeeCode() );

        employee.setName( eDTO.getName() == null ? employee.getName() : eDTO.getName() );

        employee.setSurname( eDTO.getSurname() == null ? employee.getSurname() : eDTO.getSurname() );

        employee.setAssumptionDate( eDTO.getAssumptionDate() == null ? employee.getAssumptionDate() : eDTO.getAssumptionDate() );

        employee.setBirthDate( eDTO.getBirthDate() == null ? employee.getBirthDate() : eDTO.getBirthDate() );

        employee.setUnilavCode( eDTO.getUnilavCode() == null ? employee.getUnilavCode() : eDTO.getUnilavCode() );

        employee.setBlockedFrom( eDTO.getBlockedFrom() == null ? employee.getBlockedFrom() : eDTO.getBlockedFrom() );

        employee.setBlockedTo( eDTO.getBlockedTo() == null ? employee.getBlockedTo() : eDTO.getBlockedTo() );


        employee.setUserId( user.getId() );

        Project project = projectService.getProjectByName( eDTO.getProjectName() );

        if( project == null ) {
            project = employee.getProject();
        }

        employee.setProject( project );

    }

    //Questo metodo restituisce una pagedResponseDTO ricevendo una lista di EmployeeDTO e una Page di Employee,e viene utilizzato nel getAllEmployees
    private PagedResponseDTO<EmployeeDTO> getPagedResponseDTOFromEmployeeDTO( List<EmployeeDTO> eDTOList, Page<Employee> employeePageList ) {
        PagedResponseDTO<EmployeeDTO> employeeResponseDTO = new PagedResponseDTO<>();

        employeeResponseDTO.setContent( eDTOList );

        employeeResponseDTO.setPageNo( employeePageList.getNumber() );

        employeeResponseDTO.setPageSize( employeePageList.getSize() );

        employeeResponseDTO.setTotalElements( employeePageList.getTotalElements() );

        employeeResponseDTO.setTotalPages( employeePageList.getTotalPages() );

        employeeResponseDTO.setLast( employeePageList.isLast() );

        return employeeResponseDTO;
    }

}
