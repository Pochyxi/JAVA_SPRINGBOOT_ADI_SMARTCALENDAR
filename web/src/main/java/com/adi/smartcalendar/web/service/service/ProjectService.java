package com.adi.smartcalendar.web.service.service;

import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.web.dto.ProjectDTO;
import com.adi.smartcalendar.web.entity.Project;

import java.util.List;

public interface ProjectService {

    //VOID RETURNS
    void createProject( ProjectDTO projectDTO);



    void deleteProject(Long projectId);

    void modifyProject(Long projectId, ProjectDTO projectDTO);

    //PROJECT RETURNS
    Project save ( Project project);

    Project getProjectById(Long id);

    Project getProjectByName(String name);

    //BOOLEAN RETURNS

    Boolean existsById(Long id);

    Boolean existsByName(String name);

    //DTO RETURNS

    List<ProjectDTO>getAllProjectDTOs();

    ProjectDTO getProjectDTOById(Long projectId);

    ProjectDTO getProjectDTOByName(String name);

    ProjectDTO mapProjectToDTO(Project project);

    PagedResponseDTO<ProjectDTO> getAllProjects( int pageNo, int pageSize, String sortBy, String sortDir);


}
