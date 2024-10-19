package com.adi.smartcalendar.web.controller;

import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smartcalendar/user")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById( @PathVariable("id") Long id ) {
        return ResponseEntity.ok( userService.findById( id ) );
    }
}
