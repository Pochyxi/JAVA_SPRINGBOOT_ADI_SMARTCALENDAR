package com.adi.smartcalendar.web.repository;

import com.adi.smartcalendar.web.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findById( Long id );

    Page<Employee> findByUserProfilePowerGreaterThanEqual( int power, Pageable page );

    Set<Employee> findByProjectId( Long projectId );

    Set<Employee> findByProjectName( String projectName );

    Page<Employee> findByProjectName( Pageable pageable, String projectName );

    Page<Employee> findByProjectNameAndUserEmailContains( Pageable pageable, String projectName, String userEmail );

   void deleteByUserId(Long userId);

}
