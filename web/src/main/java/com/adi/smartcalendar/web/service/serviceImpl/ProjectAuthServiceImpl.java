package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.security.dto.UserDTOInternal;
import com.adi.smartcalendar.security.service.UserService;
import com.adi.smartcalendar.web.entity.Employee;
import com.adi.smartcalendar.web.service.service.EmployeeService;
import com.adi.smartcalendar.web.service.service.ProjectAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service("projectAuthService")
@RequiredArgsConstructor
public class ProjectAuthServiceImpl implements ProjectAuthService {

    private final UserService userService;
    private final EmployeeService employeeService;

    //Metodo che controlla se l'utente può accedere alla view del Progetto
    @Override
    public boolean canViewProject(Long projectId, String permissionName) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Set<String> authoritiesAsStrings = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean hasPermission = authoritiesAsStrings
                .stream()
                .anyMatch((permission) -> permission.equals(permissionName));

        if (hasPermission) {
            return true;
        } else {
            UserDTOInternal user = userService.getUserByAuthentication();
            Employee employee = employeeService.getEmployeeById(user.getId());
            return Objects.equals(employee.getProject().getId(),projectId);
        }

    }
}
