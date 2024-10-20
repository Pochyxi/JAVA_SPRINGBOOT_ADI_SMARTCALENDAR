package com.adi.smartcalendar.web.entity;

import com.adi.smartcalendar.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name="EMPLOYEES")
public class Employee {

    @Id
    private Long id;

    @Column(name="employee_code")
    private String employeeCode;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="surname", nullable = false)
    private String surname;

    @Temporal(TemporalType.DATE)
    private LocalDate assumptionDate;

    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    private String unilavCode;

    @Temporal(TemporalType.DATE)
    private LocalDate blockedFrom;

    @Temporal(TemporalType.DATE)
    private LocalDate blockedTo;

    @Transient
    private User user;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn
    private Project project;

    @OneToMany(mappedBy = "employee", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH }, orphanRemoval = true)
    private Set<Reservation> reservations;
}
