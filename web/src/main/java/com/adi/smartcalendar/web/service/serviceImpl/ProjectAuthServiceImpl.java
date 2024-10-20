package com.adi.smartcalendar.web.service.serviceImpl;

import com.axcent.entity.Employee;
import com.axcent.security.entity.User;
import com.axcent.security.service.UserService;
import com.axcent.service.EmployeeService;
import com.axcent.service.ProjectAuthService;
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
            User user = userService.getUserByAuthentication();
            Employee employee = employeeService.getEmployeeById(user.getId());
            return Objects.equals(employee.getProject().getId(),projectId);
        }

    }
}
