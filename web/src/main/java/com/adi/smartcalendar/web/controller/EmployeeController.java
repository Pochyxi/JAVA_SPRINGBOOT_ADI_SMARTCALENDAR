package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.web.dto.EmployeeDTO;
import com.adi.smartcalendar.web.service.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    /**
     * GET EMPLOYEE BY ID
     * @param id id dell'utente
     * @return ResponseEntity con i dati dell'utente
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@authorityService.userHasPowerOnSubject(#id, 'USER_READ')")
    public ResponseEntity<EmployeeDTO> getEmployeeByUserId( @PathVariable("id") Long id) {

        return new ResponseEntity<>(employeeService.getEmployeeDTOByUserId(id), HttpStatus.OK);
    }


    /**
     * GET ALL EMPLOYEES
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy campo su cui ordinare
     * @param sortDir direzione dell'ordinamento
     * @return ResponseEntity con la lista di utenti
     */
    @GetMapping(value = "/all")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<PagedResponseDTO<EmployeeDTO>> getAllEmployees(
            @RequestParam(value = "pageNo", defaultValue = "${app.security.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.security.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.security.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.security.pagination.default_sortDirection}") String sortDir
    ) {
        return new ResponseEntity<>(employeeService.getAllEmployees(pageNo, pageSize, sortBy, sortDir), HttpStatus.OK);
    }


    /**
     * GET ALL EMPLOYEES BY PROJECT NAME
     * @param projectName nome del progetto
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy campo su cui ordinare
     * @param sortDir direzione dell'ordinamento
     * @return ResponseEntity con la lista di utenti
     */
    @GetMapping(value = "/project/{projectName}/all")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllEmployeesByProjectName(
            @PathVariable String projectName,
            @RequestParam(value = "pageNo", defaultValue = "${app.security.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.security.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.security.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.security.pagination.default_sortDirection}") String sortDir
    ) {
        return new ResponseEntity<>(employeeService.getAllUsersByProjectName(projectName,pageNo, pageSize, sortBy, sortDir), HttpStatus.OK);
    }


    /**
     * GET ALL EMPLOYEES BY PROJECT NAME
     * @param projectName nome del progetto
     * @return ResponseEntity con la lista di utenti
     */
    @GetMapping(value = "/project-name/{projectName}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Set<EmployeeDTO>> getAllEmployeesByProjectName(
            @PathVariable String projectName
    ) {

        return new ResponseEntity<>(employeeService.findAllEmployeesByProjectName(projectName), HttpStatus.OK);
    }


    /**
     * GET ALL EMPLOYEES BY PROJECT ID
     * @param projectId id del progetto
     * @return ResponseEntity con la lista di utenti
     */
    @GetMapping(value = "/project/{projectId}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<Set<EmployeeDTO>> getAllEmployeesByProjectId(
            @PathVariable Long projectId
    ) {

        return new ResponseEntity<>(employeeService.findAllEmployeesByProjectId(projectId), HttpStatus.OK);
    }


    /**
     * GET ALL EMPLOYEES BY PROJECT NAME AND EMAIL
     * @param projectName nome del progetto
     * @param email email dell'utente
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy campo su cui ordinare
     * @param sortDir direzione dell'ordinamento
     * @return ResponseEntity con la lista di utenti
     */
    @GetMapping(value = "/project-name/{projectName}/email/{email}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllEmployeesByProjectNameAndEmailContains(
            @PathVariable String projectName,
            @PathVariable String email,
            @RequestParam(value = "pageNo", defaultValue = "${app.security.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.security.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.security.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.security.pagination.default_sortDirection}") String sortDir
    ) {

        return new ResponseEntity<>(employeeService.getAllUsersByProjectNameAndEmailContains(projectName,email,pageNo,pageSize,sortBy,sortDir), HttpStatus.OK);
    }


    /**
     * CREATE EMPLOYEE
     * @param eDTO dati dell'utente
     * @return ResponseEntity con i dati dell'utente
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER_CREATE') ")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO eDTO) {
        return new ResponseEntity<>(employeeService.createEmployee(eDTO), HttpStatus.CREATED);
    }


    /**
     * MODIFY EMPLOYEE
     * @param id id dell'utente
     * @param eDTO dati dell'utente
     * @return ResponseEntity con i dati dell'utente
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE') ")
    public ResponseEntity<EmployeeDTO> modifyEmployee(@PathVariable("id") Long id,
                                                      @RequestBody EmployeeDTO eDTO) {
        return new ResponseEntity<>(employeeService.modifyEmployee(id, eDTO), HttpStatus.OK);

    }


    /**
     * REMOVE BLOCK TO EMPLOYEE
     * @param employeeId id dell'utente
     * @return ResponseEntity
     */
    @PutMapping("/remove-block/{employeeId}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void> removeBlockFromEmployee(
            @PathVariable Long employeeId){
        employeeService.removeBlockFromEmployee(employeeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * MOVE EMPLOYEE TO PROJECT
     * @param employeeId id dell'utente
     * @param projectId id del progetto
     * @return ResponseEntity
     */
    @PutMapping("/{employeeId}/move-employee/{projectId}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<Void> moveEmployeeToProject(
            @PathVariable Long employeeId,
            @PathVariable Long projectId
    ) {
        employeeService.moveEmployeeToProject(projectId, employeeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * DELETE EMPLOYEE
     * @param id id dell'utente
     * @return ResponseEntity
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE') ")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
