package com.adi.smartcalendar.web.repository;

import com.adi.smartcalendar.web.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client,Long> {

    Optional<Client> findById(Long id);

    List<Client> findAll();
}
