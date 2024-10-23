package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.web.dto.ProjectDTO;
import com.adi.smartcalendar.web.service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * GET PROJECT BY ID
     * @param id id del progetto
     * @return ResponseEntity con il progetto
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@projectAuthService.canViewProject(#id, 'PROJECT_READ')")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(projectService.getProjectDTOById(id), HttpStatus.OK);
    }


    /**
     * GET ALL PROJECTS
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy campo su cui ordinare
     * @param sortDir direzione dell'ordinamento
     * @return ResponseEntity con la lista di progetti
     */
    @GetMapping(value = "/all")
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    public ResponseEntity<PagedResponseDTO<ProjectDTO>> getAllProjects(
            @RequestParam(value = "pageNo", defaultValue = "${app.security.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.security.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.security.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.security.pagination.default_sortDirection}") String sortDir

    ) {
        return new ResponseEntity<>(projectService.getAllProjects(pageNo, pageSize, sortBy, sortDir), HttpStatus.OK);
    }


    /**
     * CREATE PROJECT
     * @param projectDTO DTO del progetto
     * @return ResponseEntity con il progetto creato
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PROJECT_CREATE') ")
    public ResponseEntity<Void> createProject(@RequestBody ProjectDTO projectDTO) {
        projectService.createProject(projectDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * MODIFY PROJECT
     * @param id id del progetto
     * @param pDTO DTO del progetto
     * @return ResponseEntity con il progetto modificato
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') ")
    public ResponseEntity<ProjectDTO> modifyProject(@PathVariable("id") Long id,
                                                    @RequestBody ProjectDTO pDTO) {
        projectService.modifyProject(id, pDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * DELETE PROJECT
     * @param id id del progetto
     * @return ResponseEntity con OK
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('PROJECT_DELETE') ")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}


