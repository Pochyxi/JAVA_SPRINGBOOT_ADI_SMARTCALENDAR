package com.adi.smartcalendar.web.repository;

import com.adi.smartcalendar.web.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfficeRepository extends JpaRepository<Office, Long> {


    Optional<Office> findByAddress(String address);



}
