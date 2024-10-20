package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.dto.ProfileDTO;
import com.adi.smartcalendar.security.dto.ProfilePermissionDTO;
import com.adi.smartcalendar.security.dto.UserDTOInternal;
import com.adi.smartcalendar.security.service.UserService;
import com.adi.smartcalendar.web.dto.*;
import com.adi.smartcalendar.web.dto.smartMonthPlanDto.*;
import com.adi.smartcalendar.web.service.service.*;
import com.adi.smartcalendar.web.service.utils.EntitiesMockInit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SmartMonthPlanServiceImpl implements SmartMonthPlanService {

    private final CalendarService calendarService;
    private final ReservationService reservationService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    private final UserService userService;
    private final OfficeService officeService;
    private final ClientService clientService;

    // METODO CHE RAGGRUPPA LE RESERVATION PER EMPLOYEEID E CALENDARID
    private Map<String, List<ReservationDTO>> preProcessReservations(int month, int year) {
        // RECUPERO TUTTE LE RESERVATION DEL MESE E ANNO RICHIESTI
        List<ReservationDTO> allReservations = reservationService.getAllByMonthAndYear(month, year);

        // CREO UN MAP CHE CONTERRA' TUTTE LE RESERVATION RAGGRUPPATE PER EMPLOYEEID E CALENDARID
        Map<String, List<ReservationDTO>> reservationMap = new HashMap<>();

        // PER OGNI RESERVATION CREO UNA CHIAVE COMPOSTA DA EMPLOYEEID E CALENDARID
        for (ReservationDTO reservation : allReservations) {
            // LA CHIAVE E' COMPOSTA DA EMPLOYEEID E CALENDARID
            String key = reservation.getEmployeeId() + "-" + reservation.getCalendarId();

            // SE LA CHIAVE NON ESISTE NEL MAP LA AGGIUNGO E CREO UNA NUOVA LISTA DI RESERVATION
            reservationMap.putIfAbsent(key, new ArrayList<>());

            // AGGIUNGO LA RESERVATION ALLA LISTA DI RESERVATION DELLA CHIAVE
            reservationMap.get(key).add(reservation);
        }

        return reservationMap;
    }

    private Map<String,List<ReservationDTO>> createCalendarClientMapReservation(int month, int year) {

        List<ReservationDTO> allReservations = reservationService.getAllByMonthAndYear(month,year);

        Map<String, List<ReservationDTO>> reservationMap = new HashMap<>();

        for(ReservationDTO reservationDTO : allReservations){

            String key = reservationDTO.getClientId() + "-" + reservationDTO.getCalendarId();

            reservationMap.putIfAbsent(key, new ArrayList<>());

            reservationMap.get(key).add(reservationDTO);
        }
        return reservationMap;
    }

    // METODO CHE RAGGRUPPA LE RESERVATION PER CALENDARID E OFFICEID
    private Map<String,List<ReservationDTO>> createCalendarOfficeMapReservation(int month,int year){

        List<ReservationDTO> allReservations = reservationService.getAllByMonthAndYear(month,year);

        Map<String, List<ReservationDTO>> reservationMap = new HashMap<>();

        // PER OGNI RESERVATION CREO UNA CHIAVE COMPOSTA DA CALENDARID E OFFICEID
        for (ReservationDTO reservation : allReservations) {
            // LA CHIAVE E' COMPOSTA DA EMPLOYEEID E CALENDARID
            String key = reservation.getOfficeId() + "-" + reservation.getCalendarId();

            // SE LA CHIAVE NON ESISTE NEL MAP LA AGGIUNGO E CREO UNA NUOVA LISTA DI RESERVATION
            reservationMap.putIfAbsent(key, new ArrayList<>());

            // AGGIUNGO LA RESERVATION ALLA LISTA DI RESERVATION DELLA CHIAVE
            reservationMap.get(key).add(reservation);
        }

        return reservationMap;
    }

    public SmartMonthPlanDTOV2 getSmartMonthPlanV2( int month, int year) {


        int timeLimit = 15;


        //RECUPERO L'AUTENTICATION DELL'UTENTE CHE FA LA RICHIESTA
        UserDTOInternal user = userService.getUserByAuthentication();
        ProfileDTO profile = userService.getProfile( user.getId() );

        Set<ProfilePermissionDTO> profilePermissions = userService.getProfilePermissions( profile.getUserId() );
        // Creazione del DTO principale che verrà restituito.
        SmartMonthPlanDTOV2 smartMonthPlanDTOV2 = new SmartMonthPlanDTOV2();
        // INIZIALIZZO UNA LISTA DI CALENDARPROJECTDTO CHE CONTERRA' TUTTI I CALENDARI DEL MESE E ANNO RICHIESTI
        smartMonthPlanDTOV2.setProjectEmployeeDTOList(new ArrayList<>());
        // RECUPERO TUTTI I PROGETTI
        List<ProjectDTO> projectDTOS = projectService.getAllProjectDTOs();
        //SE L'UTENTE NON E' ADMIN FILTRO LA LISTA DEI PROGETTI RESTITUENDO SOLO QUELLA APPARTENENTE AL SOGGETTO
        if (!profile.getName().equalsIgnoreCase("ADMIN")) {

            projectDTOS = projectDTOS.stream()

                    .filter(projectDTO -> projectDTO.getName().equals(employeeService.getEmployeeById(user.getId()).getProject().getName())).toList();

        }
        // RECUPERO TUTTI I CALENDARI DEL MESE E ANNO RICHIESTI(UN MAP CON CHIAVE COMPOSTA DA IDEMPLOYEE-IDCALENDAR)
        Map<String, List<ReservationDTO>> reservationMap = preProcessReservations(month, year);

        // RECUPERO TUTTI I GIORNI DEL MESE E ANNO RICHIESTI
        List<LocalDate> daysOfMonth = getAllDaysOfMonth(month, year);

        // RECUPERO TUTTI I GIORNI DI FESTA
        List<LocalDate> holidays = EntitiesMockInit.getHolidays(year);
        // RECUPERO TUTTI I GIORNI SENZA FESTE E SABATI E DOMENICHE
        List<LocalDate> workingDaysWithoutHolidays = daysOfMonth.stream()
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY && !holidays.contains(date))
                .toList();

        int numberOfWorkingDaysWithoutHolidays = workingDaysWithoutHolidays.size();

        int numberOfDoneReservationByProject;

        int totalNumberOfWorkingDaysWithoutHolidays = 0;

        int totalNumberOfDoneReservationByProject = 0;

        // COSTRUISCO IL DTO PRINCIPALE COMBINANDO LE INFORMAZIONI DEI CALENDARI, DEI PROGETTI E DELLE RESERVATION
        int totalNumberOfEmployees = 0;

        for (ProjectDTO project : projectDTOS) {
            // PER OGNI PROGETTO CREO UN PROJECTEMPLOYEEDTO
            ProjectEmployeeDTOV2 projectEmployeeDTOV2 = new ProjectEmployeeDTOV2();

            // SETTO IL NOME DEL PROGETTO
            projectEmployeeDTOV2.setProjectName(project.getName());

            // CREO UNA LISTA DI EMPLOYEERESERVATIONDTO CHE CONTERRA' TUTTI GLI EMPLOYEE DEL PROGETTO
            projectEmployeeDTOV2.setEmployeeDTOList(new ArrayList<>());

            // RECUPERO TUTTI GLI EMPLOYEE DEL PROGETTO
            Set<EmployeeDTO> employees = employeeService.findAllEmployeesByProjectName(project.getName());

            // COSTRUISCO UN EMPLOYEERESERVATIONDTO PER OGNI EMPLOYEE
            for (EmployeeDTO employee : employees) {
                // PER OGNI EMPLOYEE CREO UN EMPLOYEERESERVATIONDTO
                EmployeeReservationDTOV2 employeeDTOV2 = new EmployeeReservationDTOV2();

                //SETTO L'EMPLOYEEDTO
                employeeDTOV2.setEmployeeDTO(employee);

                // SETTO LA LISTA DI CALENDARRESERVATION
                employeeDTOV2.setCalendarReservationList(new ArrayList<>());

                // COSTRUISCO UN CALENDARRESERVATION PER OGNI GIORNO DEL MESE
                for (LocalDate day : daysOfMonth) {
                    // PER OGNI GIORNO CREO UN CALENDARRESERVATION
                    CalendarReservation calendarReservation = new CalendarReservation();
                    // SETTO LA DATA
                    calendarReservation.setDate(String.valueOf(day));

                    // CREO LA CHIAVE PER RECUPERARE LA RESERVATION
                    String reservationKey = employee.getUserId() + "-" + getCalendarIdFromDay(day);

                    // RECUPERO LA RESERVATION DELL'EMPLOYEE E DEL CALENDARIO RICHIESTI
                    ReservationDTO reservation = findReservationForDay(reservationMap, reservationKey, day);

                    // SETTO LA RESERVATION NEL CALENDARRESERVATION
                    calendarReservation.setReservationDTO(reservation);

                    // AGGIUNGO IL CALENDARRESERVATION ALLA LISTA DI CALENDARRESERVATION DELL'EMPLOYEERESERVATIONDTO
                    employeeDTOV2.getCalendarReservationList().add(calendarReservation);
                }

                // AGGIUNGO L'EMPLOYEERESERVATIONDTO ALLA LISTA DI EMPLOYEERESERVATIONDTO DEL PROJECTEMPLOYEEDTO
                projectEmployeeDTOV2.getEmployeeDTOList().add(employeeDTOV2);
            }
            // AGGIUNGO IL PROJECTEMPLOYEEDTO ALLA LISTA DI PROJECTEMPLOYEEDTO DEL DTO PRINCIPALE
            smartMonthPlanDTOV2.getProjectEmployeeDTOList().add(projectEmployeeDTOV2);

            int numberOfEmployees = projectEmployeeDTOV2.getEmployeeDTOList().size();

            // RECUPERO IL NUMERO DELLE PRENOTAZIONI FATTE PER PROGETTO
            numberOfDoneReservationByProject = projectEmployeeDTOV2.getEmployeeDTOList().stream()
                    .flatMap(employeeDTO -> employeeDTO.getCalendarReservationList().stream())
                    .filter(calendarReservation -> calendarReservation != null && calendarReservation.getReservationDTO() != null)
                    .mapToInt(calendarReservation -> 1) // OGNI RESERVATION CONTA COME 1
                    .sum();

            //PERCENTUALE PRENOTAZIONI PER PROGETTO
            int percentageByProject = (int) Math.round((double) numberOfDoneReservationByProject / (numberOfWorkingDaysWithoutHolidays * numberOfEmployees) * 100);

            //SETTO LA PERCENTUALE IN PROJECTEMPLOYEE
            projectEmployeeDTOV2.setReservedPercentage(percentageByProject);

            //SETTO IL NUMERO DELLE PRENOTAZIONI TOTALI
            totalNumberOfDoneReservationByProject += numberOfDoneReservationByProject;

            //SETTO IL NUMERO TOTALE DEI DIPENDENTI
            totalNumberOfEmployees += numberOfEmployees;

        }

        //SETTO IL NUMERO TOTALE DEI GIORNI MOLTIPLICATO IL NUMERO DEGLI IMPIEGATI
        totalNumberOfWorkingDaysWithoutHolidays = numberOfWorkingDaysWithoutHolidays * totalNumberOfEmployees;

        //PERCENTUALE TOTALE
        int totalPercentage = (int) Math.round((double) totalNumberOfDoneReservationByProject / totalNumberOfWorkingDaysWithoutHolidays * 100);

        //SETTO LA PERCENTUALE TOTALE NELLO SMARTMONTHPLAN
        smartMonthPlanDTOV2.setTotalReservedPercentage(totalPercentage);

        //DICHIARO LA LISTA DI OFFICEINFO
        List<OfficeInfo> officeInfoList = new ArrayList<>();

        //RECUPERO TUTTI GLI UFFICI
        List<OfficeDTO> officeDTOList = officeService.getAllOffices();

        //RECUPERO UNA MAP COMPOSTA DA UNA CHIAVE (ID UFFICIO+ID CALENDAR) E UN VALORE(LISTA DI PRENOTAZIONI)
        //CHE SI PRENDE COME PARAMETRI DI INGRESSO IL MESE E L'ANNO
        Map<String, List<ReservationDTO>> map = createCalendarOfficeMapReservation(month, year);

        //CICLO PER OGNI UFFICIO
        for (OfficeDTO office : officeDTOList) {

            OfficeInfo officeInfo = new OfficeInfo();

            //SETTO L'OFFICE IN OFFICEINFO
            officeInfo.setOfficeDTO(office);

            //DICHIARO UNA LIST DI CALENDARINFO
            List<CalendarInfo> calendarInfoList = new ArrayList<>();
            ;

            //CICLO PER OGNI GIORNO DEL MESE E DELL'ANNO
            for (LocalDate day : getAllDaysOfMonth(month, year)) {

                CalendarInfo calendarInfo = new CalendarInfo();

                //SETTO IL GIORNO DI CALENDARINFO
                calendarInfo.setCalendarDay(getCalendarIdFromDay(day));

                //DICHIARO LA KEY PER ACCEDERE ALLA LISTA DI PRENOTAZIONI DELLA MAP
                String officeReservationKey = officeInfo.getOfficeDTO().getId() + "-" + calendarInfo.getCalendarDay();

                //ACCEDO ALLA LISTA DI PRENOTAZIONI DELLA MAP
                List<ReservationDTO> reservations = map.get(officeReservationKey);

                //RECUPERO IL NUMERO DEI POSTI PRENOTATI IN BASE AL SIZE DELLA LISTA
                int reservedWorkspaces = (reservations != null) ? reservations.size() : 0;

                //SETTO I POSTI PRENOTATI IN CALENDARINFO
                calendarInfo.setReservedWorkspaces(reservedWorkspaces);

                //AGGIUNGO ALLA LISTA CALENDARINFO
                calendarInfoList.add(calendarInfo);
            }
            //IMPOSTO LA LISTA DI CALENDARINFO IN OFFICEINFO
            officeInfo.setCalendarInfoList(calendarInfoList);

            //AGGIUNGO OFFICEINFO ALLA LISTA
            officeInfoList.add(officeInfo);

        }

        //IMPOSTO LA LISTA DI OFFICEINFO IN SMARTMONTHPLANDTOV2
        smartMonthPlanDTOV2.setOfficeInfoList(officeInfoList);

        List<ClientInfo> clientInfoList = new ArrayList<ClientInfo>();

        // RECUPERO LA LISTA DI TUTTI I CLIENTDTO
        List<ClientDTO> clientDTOList = clientService.getAllClient();

        //RECUPERO UNA MAP COMPOSTA DA UNA CHIAVE (ID CLIENT+ID CALENDAR) E UN VALORE(LISTA DI PRENOTAZIONI)
        //CHE SI PRENDE COME PARAMETRI DI INGRESSO IL MESE E L'ANNO
        Map<String, List<ReservationDTO>> mapClientReservation = createCalendarClientMapReservation(month, year);

        for (ClientDTO clientDTO : clientDTOList) {

            ClientInfo clientInfo = new ClientInfo();

            clientInfo.setClientDTO(clientDTO);

            //DICHIARO UNA LIST DI CALENDARINFO
            List<CalendarInfo> clientCalendarInfoList = new ArrayList<>();
            ;

            for (LocalDate day : getAllDaysOfMonth(month, year)) {

                CalendarInfo clientCalendarInfo = new CalendarInfo();

                //SETTO IL GIORNO DI CALENDARINFO
                clientCalendarInfo.setCalendarDay(getCalendarIdFromDay(day));

                //DICHIARO LA KEY PER ACCEDERE ALLA LISTA DI PRENOTAZIONI DELLA MAP
                String clientReservationKey = clientInfo.getClientDTO().getId() + "-" + clientCalendarInfo.getCalendarDay();

                //ACCEDO ALLA LISTA DI PRENOTAZIONI DELLA MAP
                List<ReservationDTO> reservations = mapClientReservation.get(clientReservationKey);

                //RECUPERO IL NUMERO DEI POSTI PRENOTATI IN BASE AL SIZE DELLA LISTA
                int reservedWorkspaces = (reservations != null) ? reservations.size() : 0;

                //SETTO I POSTI PRENOTATI IN CALENDARINFO
                clientCalendarInfo.setReservedWorkspaces(reservedWorkspaces);

                //AGGIUNGO ALLA LISTA CALENDARINFO
                clientCalendarInfoList.add(clientCalendarInfo);
            }
            clientInfo.setCalendarInfoList(clientCalendarInfoList);

            clientInfoList.add(clientInfo);

        }

        smartMonthPlanDTOV2.setClientInfoList(clientInfoList);


        LocalDate timeLimitDate = LocalDate.of(year, month, timeLimit);

        smartMonthPlanDTOV2.setTimeLimit(timeLimitDate);

        return smartMonthPlanDTOV2;
    }


    // METODO CHE RECUPERA LA RESERVATION DELL'EMPLOYEE E DEL CALENDARIO RICHIESTI
    private ReservationDTO findReservationForDay(Map<String, List<ReservationDTO>> reservationMap, String reservationKey, LocalDate day) {
        // RECUPERO LA LISTA DI RESERVATION DELLA CHIAVE
        List<ReservationDTO> reservations = reservationMap.getOrDefault(reservationKey, Collections.emptyList());

        // RECUPERO LA RESERVATION DEL GIORNO RICHIESTO
        // OPPURE NULL SE NON ESISTE
        return reservations.stream()
                .filter(reservation -> reservation.getCalendarId().equals(getCalendarIdFromDay(day)))
                .findFirst()
                .orElse(null);
    }

    // METODO CHE CREA UNA CHIAVE PER RECUPERARE LA RESERVATION
    private String getCalendarIdFromDay( LocalDate day ) {
        return day.getDayOfMonth() + "-" + day.getMonthValue() + "-" + day.getYear();
    }


    // METODO CHE RECUPERA TUTTI I GIORNI DEL MESE E ANNO RICHIESTI
    private List<LocalDate> getAllDaysOfMonth(int month, int year) {
        // CREO UN FORMATTER PER LA DATA
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");

        // RECUPERO TUTTI I CALENDARI DEL MESE E ANNO RICHIESTI E LI TRASFORMO IN UNA LISTA DI LOCALDATE
        return calendarService.findByMonthAndYear(month, year).stream()
                .map(calendar -> LocalDate.parse(calendar.getId(), formatter))
                .collect(Collectors.toList());
    }

}
