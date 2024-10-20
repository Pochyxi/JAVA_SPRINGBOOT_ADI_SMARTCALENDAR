package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.web.dto.ProjectDTO;
import com.adi.smartcalendar.web.service.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PROJECT_CREATE') ")
    public ResponseEntity<Void> createProject(@RequestBody ProjectDTO projectDTO) {
        projectService.createProject(projectDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE') ")
    public ResponseEntity<ProjectDTO> modifyProject(@PathVariable("id") Long id,
                                                    @RequestBody ProjectDTO pDTO) {
        projectService.modifyProject(id, pDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('PROJECT_DELETE') ")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("@projectAuthService.canViewProject(#id, 'PROJECT_READ')")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(projectService.getProjectDTOById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    public ResponseEntity<PagedResponseDTO<ProjectDTO>> getAllProjects(
            @RequestParam(value = "pageNo", defaultValue = "${app.pagination.default_pageNumber}") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "${app.pagination.default_pageSize}") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "${app.pagination.default_sortBy}", required = false) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "${app.pagination.default_sortDirection}") String sortDir

    ) {
        return new ResponseEntity<>(projectService.getAllProjects(pageNo, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

}


