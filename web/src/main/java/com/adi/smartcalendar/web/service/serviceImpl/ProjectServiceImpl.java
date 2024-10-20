package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.dto.PagedResponseDTO;
import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.web.dto.ProjectDTO;
import com.adi.smartcalendar.web.entity.Project;
import com.adi.smartcalendar.web.repository.ProjectRepository;
import com.adi.smartcalendar.web.service.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;


    // VOID RETURNS
    @Override
    public void createProject( ProjectDTO projectDTO) {
        Project project = mapProjectDTOToEntity(projectDTO);

        save(project);

    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long projectId) {
        Project project = getProjectById(projectId);
        if(! project.getEmployees().isEmpty()){
            throw new appException(HttpStatus.BAD_REQUEST, ErrorCodeList.NOT_EMPTY_PROJECT);
        }
        projectRepository.delete(project);

    }

    @Override
    public void modifyProject(Long projectId, ProjectDTO projectDTO) {

        Project project = getProjectById(projectId);

        setProjectPropertiesFromDTO(project, projectDTO);

        Project projectSaved = projectRepository.save(project);

        mapProjectToDTO(projectSaved);
    }
    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new appException(HttpStatus.BAD_REQUEST,"PROJECT " + ErrorCodeList.NF404));
    }

    @Override
    public Project getProjectByName(String name) {

        return projectRepository.findByName(name).orElse( null );
    }

    @Override
    public Boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }

    @Override
    public Boolean existsByName(String name) {
        return projectRepository.existsByName(name);
    }

    @Override
    public ProjectDTO getProjectDTOById(Long projectId) {
        Project project = getProjectById(projectId);
        return mapProjectToDTO(project);
    }

    @Override
    public ProjectDTO getProjectDTOByName(String name) {
        Project project = getProjectByName(name);

        if( project == null ) throw new appException(HttpStatus.BAD_REQUEST,"PROJECT "+ErrorCodeList.NF404);

        return mapProjectToDTO(project);
    }


    @Override
    public PagedResponseDTO<ProjectDTO> getAllProjects( int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Project> projectPageList = projectRepository.findAll(pageable);

        List<Project> projectList = projectPageList.getContent();

        List<ProjectDTO> pDTOList = projectList.stream().map(this::mapProjectToDTO).toList();

        return setProjectDTOPagedResponseDTO(pDTOList, projectPageList);


    }

    @Override
    public List<ProjectDTO> getAllProjectDTOs() {
        List<Project> projectList = projectRepository.findAll();
        return projectList.stream().map(this::mapProjectToDTO).toList();
    }


    public Project mapProjectDTOToEntity(ProjectDTO projectDTO) {
        Project project = new Project();

        project.setId(projectDTO.getId());

        project.setName(projectDTO.getName());

        project.setBlockedFrom(projectDTO.getBlockedFrom());

        project.setBlockedTo(projectDTO.getBlockedTo());

        return project;
    }
    @Override
    public ProjectDTO mapProjectToDTO(Project project) {
        ProjectDTO pDTO = new ProjectDTO();

        pDTO.setId(project.getId());

        pDTO.setName(project.getName());

        pDTO.setBlockedFrom(project.getBlockedFrom());

        pDTO.setBlockedTo(project.getBlockedTo());

        return pDTO;

    }


    private void setProjectPropertiesFromDTO(Project project, ProjectDTO pDTO) {

        project.setName(pDTO.getName() == null ? project.getName() : pDTO.getName());

        project.setBlockedFrom(pDTO.getBlockedFrom() == null ? project.getBlockedFrom() : pDTO.getBlockedFrom());

        project.setBlockedTo(pDTO.getBlockedTo() == null ? project.getBlockedTo() : pDTO.getBlockedFrom());


    }

    //Metodo che restituisce una pagedResponseDTO di project
    private PagedResponseDTO<ProjectDTO> setProjectDTOPagedResponseDTO(List<ProjectDTO> pDTOList, Page<Project> projectPageList) {
        PagedResponseDTO<ProjectDTO> projectResponseDTO = new PagedResponseDTO<>();

        projectResponseDTO.setContent(pDTOList);

        projectResponseDTO.setPageNo(projectPageList.getNumber());

        projectResponseDTO.setPageSize(projectPageList.getSize());

        projectResponseDTO.setTotalElements(projectPageList.getTotalElements());

        projectResponseDTO.setTotalPages(projectPageList.getTotalPages());

        projectResponseDTO.setLast(projectPageList.isLast());
        return projectResponseDTO;
    }
}
