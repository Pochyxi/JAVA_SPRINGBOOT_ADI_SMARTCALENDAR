package com.adi.smartcalendar.web.service.service;
import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.web.dto.EmployeeDTO;
import com.adi.smartcalendar.web.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface EmployeeService {

    // VOID RETURNS

    void save ( Employee employee);


    void createEmployee( EmployeeDTO employeeDTO);

    void moveEmployeeToProject(Long projectId, Long employeeId);

    void deleteEmployee(Long id);

    void removeBlockFromEmployee(Long employeeId);

    // EMPLOYEE RETURNS
    Employee getEmployeeByUserId(Long userId);

    Employee getEmployeeById(Long id);

    Employee mapToEntity( EmployeeDTO employeeDTO);

    Page<Employee> findByUserProfilePowerGreaterThanEqual(int power, Pageable page);


    //USER RETURNS
    PagedResponseDTO<UserDTO> getAllUsersByProjectName( String projectName, int pageNo, int pageSize, String sortBy, String sortDir);

    PagedResponseDTO<UserDTO> getAllUsersByProjectNameAndEmailContains(String projectName,String emailCont,int pageNo, int pageSize, String sortBy, String sortDir);

    // EMPLOYEE DTO RETURNS

    EmployeeDTO getEmployeeDTOByUserId(Long userId);

    Set<EmployeeDTO> findAllEmployeesByProjectId(Long projectId);

    Set<EmployeeDTO> findAllEmployeesByProjectName(String projectName);

    PagedResponseDTO<EmployeeDTO> getAllEmployees(int pageNo, int pageSize, String sortBy, String sortDir);

    EmployeeDTO modifyEmployee(Long userId, EmployeeDTO employeeDTO);

    EmployeeDTO mapEmployeeToDTO(Employee employee);

}
