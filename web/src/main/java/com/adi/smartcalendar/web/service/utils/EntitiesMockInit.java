package com.adi.smartcalendar.web.service.utils;

import com.adi.smartcalendar.security.dto.UserDTO;
import com.adi.smartcalendar.security.service.UserService;
import com.adi.smartcalendar.web.dto.*;
import com.adi.smartcalendar.web.entity.Employee;
import com.adi.smartcalendar.web.repository.CalendarRepository;
import com.adi.smartcalendar.web.repository.ClientRepository;
import com.adi.smartcalendar.web.repository.ProjectRepository;
import com.adi.smartcalendar.web.service.service.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class EntitiesMockInit {

    private static final Logger logger = LoggerFactory.getLogger(EntitiesMockInit.class);

    private final UserService userService;

    private final ProjectService projectService;

    private final EmployeeService employeeService;
    private final ProjectRepository projectRepository;

    private final CalendarService calendarService;
    private final CalendarRepository calendarRepository;
    private final OfficeService officeService;
    private final ClientRepository clientRepository;

    private final ClientService clientService;

    @Autowired
    public EntitiesMockInit(UserService userService, ProjectService projectService,
                            EmployeeService employeeService,
                            CalendarService calendarService,
                            ProjectRepository projectRepository,
                            CalendarRepository calendarRepository,
                            OfficeService officeService,
                            ClientRepository clientRepository,
                            ClientService clientService) {
        this.userService = userService;
        this.projectService = projectService;
        this.employeeService = employeeService;
        this.projectRepository = projectRepository;
        this.calendarService = calendarService;
        this.calendarRepository = calendarRepository;
        this.officeService = officeService;
        this.clientRepository = clientRepository;
        this.clientService = clientService;
    }

    //Crea 3 progetti di cui uno sarà quello Pending
    @PostConstruct
    public void initProjects() {
        long numberOfProjects = projectRepository.count();

        logger.info("NUMERO PROGETTI: " + numberOfProjects);


        ProjectDTO project = new ProjectDTO();
        project.setName("Pending");

        if (existingProject(project.getName())) {
            logger.info("PROGETTO " + project.getName() + " ESISTENTE");

        }else{
            projectService.createProject(project);
            logger.info("PENDING PROJECT CREATO");
        }


        ProjectDTO project1 = new ProjectDTO();
        project1.setName("Engineering");
        if (existingProject(project1.getName())) {
            logger.info("PROGETTO " + project1.getName() + " ESISTENTE");

        }else{
            projectService.createProject(project1);
            logger.info("PROGETTO ENGINEERING CREATO");
        }

        ProjectDTO project2 = new ProjectDTO();
        project2.setName("Treni");
        if (existingProject(project2.getName())) {
            logger.info("PROGETTO " + project2.getName() + " ESISTENTE");

        }else{
            projectService.createProject(project2);

            logger.info("PROGETTO TRENI CREATO");
        }
    }

    @PostConstruct
    public void initEmployees() {
        UserDTO userAdmin = userService.findByEmail("Adiener@axcent.com");
        UserDTO userDario = userService.findByEmail("Dario@axcent.com");

        linkEmployeeToUser( userAdmin.getId(), "Adiener", "Lopez Velazquez" );
        linkEmployeeToUser( userDario.getId(), "Dario", "Corrado" );
    }

    @PostConstruct
    public void initCalendar() {
        long numberOfCalendars = calendarRepository.count();

        if (numberOfCalendars < 1) {
            logger.info("GENERAZIONE CALENDARIO");
            int yearsToGenerate = 10;

            int currentYear = LocalDate.now().getYear();

            for (int year = currentYear; year < currentYear + yearsToGenerate; year++) {
                for (int month = 1; month <= 12; month++) {
                    int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
                    for (int day = 1; day <= daysInMonth; day++) {
                        CalendarDTO calendar = getCalendarDTO( year, month, day );

                        calendarService.createCalendar(calendar);
                    }


                }

            }
            logger.info("CALENDARIO CREATO");
        }
        logger.info("CALENDARIO ESISTENTE");
    }

    @PostConstruct
    public void initOffices() {
        OfficeDTO mascagniDTO = OfficeDTO.builder()
                .address( "Via Mascagni 64 - 80128. NAPOLI" )
                .name("M")
                .workstations( 10 )
                .build();

        OfficeDTO immacolataDTO = OfficeDTO.builder()
                .address( "Piazza dell'Immacolata 4 - 80129. NAPOLI" )
                .name( "I")
                .workstations( 10 )
                .build();

        createOfficeIfNotPresent( mascagniDTO );
        createOfficeIfNotPresent( immacolataDTO );
    }

    // PRIVATE METHODS
    private static CalendarDTO getCalendarDTO( int year, int month, int day ) {
        LocalDate currentDate = LocalDate.of( year, month, day );
        CalendarDTO calendar = new CalendarDTO();
        calendar.setId( day + "-" + month + "-" + year );
        calendar.setDay( day );
        calendar.setMonth( month );
        calendar.setYear( year );

        //Se DOMENICA O SABATO SETTA HOLIDAY TRUE
        if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY || currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            calendar.setHoliday(true);
        }

        //SE LA DATA CORRISPONDE AD UNA FESTIVITA' SETTA HOLIDAY TRUE
        if (getHolidays(year).stream().anyMatch(holiday -> holiday.equals(currentDate))) {
            calendar.setHoliday(true);
        }


        return calendar;
    }


    //METODO CHE RESTITUISCE UNA LISTA DI DATE CORRISPONDENTI ALLE FESTIVITA' ITALIANE
    public static List<LocalDate> getHolidays(int year) {
        List<LocalDate> holidays = new ArrayList<>();

        // CAPODANNO
        holidays.add(LocalDate.of(year, 1, 1));

        // EPIFANIA
        holidays.add(LocalDate.of(year, 1, 6));

        // LIBERAZIONE
        holidays.add(LocalDate.of(year, 4, 25));

        // FESTA DEI LAVORATORI
        holidays.add(LocalDate.of(year, 5, 1));

        // FESTA DELLA REPUBBLICA
        holidays.add(LocalDate.of(year, 6, 2));

        // FERRAGOSTO
        holidays.add(LocalDate.of(year, 8, 15));

        // TUTTI I SANTI
        holidays.add(LocalDate.of(year, 11, 1));

        // FESTA DELL'IMMACOLATA
        holidays.add(LocalDate.of(year, 12, 8));

        // NATALE
        holidays.add(LocalDate.of(year, 12, 25));

        // SANTO STEFANO
        holidays.add(LocalDate.of(year, 12, 26));

        // PASQUA
        LocalDate easter = calculateEaster(year);
        holidays.add(easter);

        // PASQUETTA
        LocalDate easterMonday = easter.plusDays(1);
        holidays.add(easterMonday);

        return holidays;
    }

    //METODO PER CALCOLARE LA PASQUA
    private static LocalDate calculateEaster(int year) {
        int f = year / 100;
        int G = year % 19;
        int C = f / 4;
        int H = (8 * f + 13) / 25 - 2;
        int I = (19 * G + f - C - H + 15) % 30;
        I = I - (I / 28) * (1 - (I / 28) * (29 / (I + 1)) * ((21 - G) / 11));
        int J = (year + year / 4 + I + 2 - f + C) % 7;
        int L = I - J;
        int month = 3 + (L + 40) / 44;
        int day = L + 28 - 31 * (month / 4);

        return LocalDate.of(year, month, day);
    }



    private boolean existingProject(String projectName) {
        return projectService.existsByName(projectName);
    }

    private void linkEmployeeToUser(Long userId, String name, String surname) {
        if( userId > 0) {
            Employee employee = employeeService.getEmployeeByUserId( userId );

            if( employee != null ) {
                logger.info( "EMPLOYEE ESISTENTE" );
            } else {
                // Salviamo prima il dipendente nella lista del progetto(Set)
                EmployeeDTO employeeDTO = new EmployeeDTO();
                employeeDTO.setEmployeeCode( "AX" + userId + new Random().nextInt( 100000 ) );
                employeeDTO.setName( name );
                employeeDTO.setSurname( surname );
                employeeDTO.setUnilavCode( "ULAV" + userId + new Random().nextInt( 100000 ) );
                employeeDTO.setBirthDate( LocalDate.of( 1994, 8, 27 ) );
                employeeDTO.setAssumptionDate( LocalDate.now() );
                employeeDTO.setProjectName( "Treni" );
                employeeDTO.setUserId( userId );

                employeeService.createEmployee( employeeDTO );
            }
        }
    }

    private void createOfficeIfNotPresent( OfficeDTO officeDTO) {
         if( officeService.getOfficeByAddress( officeDTO.getAddress() ) != null ) {
            logger.info( "UFFICIO ESISTENTE" );
        } else {
            officeService.createOffice( officeDTO );
        }
    }

    @PostConstruct
    public void initClient() {
        long numberOfClients = clientRepository.count();

        if( numberOfClients<1){
            ClientDTO cDTO = new ClientDTO();
            cDTO.setName("NTTD");
            clientService.createClient(cDTO);
            logger.info("CLIENT CREATO");
        }
    }

}
