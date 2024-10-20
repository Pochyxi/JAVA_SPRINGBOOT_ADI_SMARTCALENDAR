package com.adi.smartcalendar.web.entity.spec;

import com.axcent.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ReservationSpec {

    // SPIEGAZIONE METODO
    public static Specification<Reservation> findByProjectId(Long projectId) {
        return (root, query, criteriaBuilder) -> {
            if (projectId == null) {
                return criteriaBuilder.conjunction(); // Nessuna condizione
            }

            // JOIN RESERVATION -> EMPLOYEE
            Join<Reservation, Employee> employeeJoin = root.join(Reservation_.EMPLOYEE);

            // JOIN EMPLOYEE -> PROJECT
            Join<Employee, Project> projectJoin = employeeJoin.join(Employee_.PROJECT);

            // Ritorna la condizione
            return criteriaBuilder.equal(projectJoin.get(Reservation_.ID), projectId);
        };
    }

    public static Specification<Reservation> findByEmployeeAndDate(Long employeeId, String dateId) {
        return (root, query, criteriaBuilder) -> {
            if (employeeId == null || dateId == null) {
                return criteriaBuilder.conjunction(); // Nessuna condizione
            }
            // Unisciti all'entità Employee
            Join<Reservation, Employee> employeeJoin = root.join(Reservation_.EMPLOYEE);

            // Unisciti all'entità Calendar
            Join<Reservation, Calendar> calendarJoin = root.join(Reservation_.CALENDAR);

            // Crea le condizioni
            Predicate employeeCondition = criteriaBuilder.equal(employeeJoin.get(Employee_.ID), employeeId);
            Predicate dateCondition = criteriaBuilder.equal(calendarJoin.get(Calendar_.ID), dateId);

            // Ritorna la condizione
            return criteriaBuilder.and(employeeCondition, dateCondition);
        };
    }
}
