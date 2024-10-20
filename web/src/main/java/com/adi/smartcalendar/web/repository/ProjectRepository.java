package com.adi.smartcalendar.web.repository;

import com.adi.smartcalendar.web.entity.Employee;
import com.adi.smartcalendar.web.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {


Optional<Project> findByName(String name);

Boolean existsByName(String name);

List<Project> findAll();


Optional<Project> findByEmployeesContains( Employee employee);






}
