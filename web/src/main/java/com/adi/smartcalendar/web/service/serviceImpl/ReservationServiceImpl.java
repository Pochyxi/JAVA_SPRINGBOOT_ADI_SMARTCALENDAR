package com.adi.smartcalendar.web.service.serviceImpl;

import com.adi.smartcalendar.security.dto.ProfileDTO;
import com.adi.smartcalendar.security.dto.ProfilePermissionDTO;
import com.adi.smartcalendar.security.dto.UserDTOInternal;
import com.adi.smartcalendar.security.enumerated.PermissionList;
import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.security.service.UserService;
import com.adi.smartcalendar.web.dto.ReservationDTO;
import com.adi.smartcalendar.web.dto.smartMonthPlanDto.MassivePrenotationDTO;
import com.adi.smartcalendar.web.entity.*;
import com.adi.smartcalendar.web.entity.Calendar;
import com.adi.smartcalendar.web.entity.enumerated.ReservationType;
import com.adi.smartcalendar.web.entity.spec.ReservationSpec;
import com.adi.smartcalendar.web.repository.ReservationRepository;
import com.adi.smartcalendar.web.service.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final EmployeeService employeeService;

    private final CalendarService calendarService;

    private final OfficeService officeService;

    private final UserService userService;

    private final ClientService clientService;


    //VOID RETURNS
    @Override
    public void save( Reservation reservation ) {

        //SE E' STATO ASSEGNATO SIA L'OFFICE CHE IL CLIENTE RITORNA UN ECCEZIONE DI AMBIGUITA'
        if( reservation.getOffice() != null && reservation.getClient() != null ) {
            throw new appException( HttpStatus.BAD_REQUEST, "OFFICE/CLIENT " + ErrorCodeList.AMBIGUOUS_FIELDS );
        }

        if( reservation.getOffice() != null && reservation.getReservationType() != ReservationType.PRESENT ) {
            throw new appException( HttpStatus.BAD_REQUEST, "RESERVATIONTYPENAME/OFFICE " + ErrorCodeList.AMBIGUOUS_FIELDS );

        }

        if( reservation.getClient() != null && reservation.getReservationType() != ReservationType.CLIENT ) {
            throw new appException( HttpStatus.BAD_REQUEST, "RESERVATIONTYPENAME/CLIENT " + ErrorCodeList.AMBIGUOUS_FIELDS );

        }

        reservationRepository.save( reservation );
    }

    @Override
    @Transactional
    public void createReservation( ReservationDTO reservationDTO ) {

        //CONTROLLO SE L'UTENTE CHE FA LA RICHIESTA (CHE NON E' ADMIN) HA LO STESSO ID DELL'UTENTE CHE VERRA' PRENOTATO
        //E SE LA PRENOTAZIONE VIENE EFFETTUATA ENTRO IL 15 DEL MESE

        if( !canManageReservation( reservationDTO.getEmployeeId() ) ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_PERMISSION );
        }

        //CONTROLLO CHE LA PRENOTAZIONE NON SIA GIA' PRESENTE
        if( !getByEmployeeAndDate( reservationDTO.getEmployeeId(), reservationDTO.getCalendarId() ).isEmpty() ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.EXISTING_RESERVATION );
        }

        //CONTROLLO CHE NON SI POSSANO FARE PRENOTAZIONI IN UN GIORNO FESTIVO
        Calendar calendarReserved = calendarService.getCalendarById( reservationDTO.getCalendarId() );
        if( calendarReserved.isHoliday() ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.IS_HOLIDAY );
        }

        Reservation reservation = new Reservation();


        //CONTROLLO CHE L'OFFICE PRESENTE NEL DTO ESISTA

        if( reservationDTO.getOfficeId() != null ) {
            if( !hasUserReservationCreatePermission() ) {
                throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_PERMISSION + " CANNOT_ASSIGN_OFFICE_OR_CLIENT" );
            }

            if( reservationDTO.getOfficeId() > 0 ) {
                Office officeFound = officeService.getOfficeById( reservationDTO.getOfficeId() )
                        .orElseThrow( () -> new appException( HttpStatus.BAD_REQUEST, "OFFICE" + ErrorCodeList.NF404 ) );
                reservation.setOffice( officeFound );
            }

        }

        // CONTROLLO CHE IL CLIENT NEL DTO ESISTA
        if( reservationDTO.getClientId() != null ) {
            if( !hasUserReservationCreatePermission() ) {
                throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_PERMISSION + " CANNOT_ASSIGN_OFFICE_OR_CLIENT" );
            }

            if( reservationDTO.getClientId() > 0 ) {
                //RECUPERO IL CLIENTE DAL DTO (SE ESISTE)
                Client clientFound = clientService.getClientById( reservationDTO.getClientId() );
                reservation.setClient( clientFound );
            }
        }


        //RECUPERO IL DIPENDENTE
        Employee employeeFound = employeeService.getEmployeeById( reservationDTO.getEmployeeId() );

        //RECUPERO IL CALENDAR
        Calendar calendar = calendarService.getCalendarById( reservationDTO.getCalendarId() );

        //IMPOSTO IL DIPENDENTE
        reservation.setEmployee( employeeFound );


        //IMPOSTO IL CALENDAR FACENDO ASSEGNARE ALL'UTENTE NON ADMIN SOLO IL GIORNO,IL MESE E L'ANNO VERRANO
        //IMPOSTATI AUTOMATICAMENTE AL MESE E ANNO SUCCESSIVO ALLA DATA IN CUI VIENE EFFETTUATO L'INSERIMENTO
        setCalendarByAuthentication( reservation, calendar );


        // CONTROLLO CHE VENGA INSERITO CORRETTAMENTE IL NOME DEL TIPO DI PRENOTAZIONE
        if( !isCorrectReservationType( reservationDTO ) || reservationDTO.getReservationTypeName() == null ) {
            throw new appException( HttpStatus.BAD_REQUEST, "RESERVATION_TYPE " + ErrorCodeList.NOT_CORRECT_TYPE );
        }

        reservation.setReservationType( ReservationType.valueOf( reservationDTO.getReservationTypeName() ) );

        save( reservation );
    }

    @Override
    @Transactional
    public void createMassivePrenotation( MassivePrenotationDTO massivePrenotationDTO ) {

        for( ReservationDTO reservationDTO : massivePrenotationDTO.getReservationDTOList() ) {
            // CONTROLLO SE LA PRENOTAZIONE ESISTE
            ReservationDTO reservationDTOFound = getByEmployeeAndDate(
                    reservationDTO.getEmployeeId(),
                    reservationDTO.getCalendarId() ).stream()
                    .filter( ( reservation ) -> Objects.equals( reservation.getEmployeeId(), reservationDTO.getEmployeeId() ) && Objects.equals( reservation.getCalendarId(), reservationDTO.getCalendarId() ) )
                    .findFirst().orElse( null );

            // SE LA PRENOTAZIONE RISULTA NON ESISTENTE
            if( reservationDTOFound == null ) {

                createReservation( reservationDTO );
            } else {
                // PER EVITARE AMBIGUITA' DEVO CONTROLLARE LE MODIFICHE EFFETTUATE ALLA PRENOTAZIONE

                // SE SMARTWORK ALLORA MI ASSICURO CHE OFFICE E CLIENT SIANO NULL
                if( Objects.equals( reservationDTO.getReservationTypeName(), ReservationType.SMARTWORK.name() ) ) {
                    reservationDTO.setOfficeId( null );
                    reservationDTO.setClientId( null );
                    // SE PRESENT ALLORA MI ASSICURO CHE CLIENT SIA NULL
                } else if( Objects.equals(
                        reservationDTO.getReservationTypeName(),
                        ReservationType.PRESENT.name() ) ) {
                    reservationDTO.setClientId( null );
                    // SE CLIENT ALLORA MI ASSICURO CHE OFFICE SIA NULL
                } else if( Objects.equals(
                        reservationDTO.getReservationTypeName(),
                        ReservationType.CLIENT.name() ) ) {
                    reservationDTO.setOfficeId( null );
                    // SE HOLIDAY ALLORA MI ASSICURO CHE OFFICE E CLIENT SIANO NULL
                } else {
                    reservationDTO.setOfficeId( null );
                    reservationDTO.setClientId( null );
                }

                modifyReservation( reservationDTOFound.getId(), reservationDTO );
            }
        }
    }

    @Override
    public void deleteReservation( Long reservationId ) {

        Reservation reservation = getReservationById( reservationId );
        UserDTOInternal user = userService.getUserByAuthentication();

        ProfileDTO profile = userService.getProfile( user.getId() );

        Set<ProfilePermissionDTO> profilePermissions = userService.getProfilePermissions( profile.getUserId() );

        boolean hasDeletePermission = profilePermissions.stream()
                .anyMatch( ( profilePermission ) -> Objects.equals(
                        profilePermission.getPermissionName(),
                        PermissionList.RESERVATION.name() ) && profilePermission.getValueDelete() == 1 );

        boolean hasSameId = Objects.equals( reservation.getEmployee().getUserId(), user.getId() );

        boolean canDelete = hasDeletePermission || hasSameId;

        //CONTROLLO SE L'UTENTE CHE EFFETTUA LA RICHIESTA DI ELIMINAZIONE ABBIA LO STESSO ID DEL DEL
        // DIPENDENTE CONTENUTO NELLA PRENOTAZIONE O CHE ABBIA I PERMESSI
        if( !canDelete ) {

            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_PERMISSION );
        }

        reservationRepository.delete( reservation );
    }

    @Override
    public void deleteReservationByEmployeeIdAndCalendarId( Long employeeId, String calendarId ) {
        Reservation reservation = reservationRepository.findByEmployeeUserIdAndCalendarId( employeeId, calendarId )
                .orElseThrow( () -> new appException( HttpStatus.BAD_REQUEST, "RESERVATION" + ErrorCodeList.NF404 ) );
        deleteReservation( reservation.getId() );

    }

    //METODO DI MODIFICA SOLO PER CHI HA LA FLAG RESERVATION_UPDATE(PUO' ASSEGNARE SOLO L'UFFICIO O IL CLIENTE)
    @Override
    public void modifyReservation( Long reservationId, ReservationDTO reservationDTO ) {
        Reservation reservation = getReservationById( reservationId );


        //SE OFFICE VIENE ASSEGNATO SETTA CLIENT NULL
        if( reservationDTO.getOfficeId() != null ) {
            Optional<Office> officeOpt = officeService.getOfficeById( reservationDTO.getOfficeId() );

            officeOpt.ifPresent( reservation::setOffice );

            reservation.setClient( null );
        } else {
            reservation.setOffice( null );
        }
        //SE CLIENT VIENE ASSEGNATO SETTA OFFICE NULL
        if( reservationDTO.getClientId() != null ) {

            reservation.setClient( clientService.getClientById( reservationDTO.getClientId() ) );

            reservation.setOffice( null );
        } else {
            reservation.setClient( null );
        }

        if( !isCorrectReservationType( reservationDTO ) || reservationDTO.getReservationTypeName() == null ) {
            throw new appException( HttpStatus.BAD_REQUEST, "RESERVATION_TYPE " + ErrorCodeList.NOT_CORRECT_TYPE );
        }

        reservation.setReservationType( ReservationType.valueOf( reservationDTO.getReservationTypeName() ) );



        save( reservation );
    }

    //ENTITY RETURNS

    @Override
    public Reservation getReservationById( Long id ) {
        return reservationRepository.findById( id )
                .orElseThrow( () -> new appException( HttpStatus.BAD_REQUEST, "RESERVATION " + ErrorCodeList.NF404 ) );

    }

    //METODO CHE RESTITUISCE UNA LISTA DI PRENOTAZIONI IN BASE ALL' UFFICIO
    @Override
    public Set<ReservationDTO> getReservationsByOfficeId( Long officeId ) {

        return reservationRepository.findByOfficeId( officeId )
                .stream().map( this::mapEntityToReservationDTO ).collect( Collectors.toSet() );
    }

    //METODO CHE RESTITUISCE UNA LISTA DI PRENOTAZIONI IN BASE AL DIPENDENTE
    @Override
    public Set<ReservationDTO> getReservationsByEmployeeId( Long employeeId ) {
        return reservationRepository.findByEmployeeUserId( employeeId )
                .stream().map( this::mapEntityToReservationDTO ).collect( Collectors.toSet() );
    }

    //METODO CHE RESTITUISCE UNA LISTA DI PRENOTAZIONI IN BASE AL PROGETTO
    @Override
    public List<ReservationDTO> getReservationsByProjectId( Long projectId ) {
        if( !canViewProjectReservation( projectId ) ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_PERMISSION );
        }

        Specification<Reservation> spec = ReservationSpec.findByProjectId( projectId );

        List<Reservation> reservations = reservationRepository.findAll( spec );

        return reservations.stream().map( this::mapEntityToReservationDTO ).toList();
    }

    @Override
    public List<ReservationDTO> findByEmployeeIdAndCalendarMonthAndCalendarDay( Long employee_id, Integer calendar_month, Integer calendar_day ) {
        return reservationRepository.findByEmployeeUserIdAndCalendarMonthAndCalendarDay( employee_id, calendar_month, calendar_day )
                .stream().map( this::mapEntityToReservationDTO ).toList();
    }

    @Override
    public List<ReservationDTO> getByEmployeeAndDate( Long employeeId, String calendarId ) {
        //CONTROLLO SE L'UTENTE HA I PERMESSI PER VEDERE LE PRENOTAZIONI
        UserDTOInternal user = userService.getUserByAuthentication();

        ProfileDTO profile = userService.getProfile( user.getId() );

        Set<ProfilePermissionDTO> profilePermissions = userService.getProfilePermissions( profile.getUserId() );

        boolean hasReservationReadPermission = profilePermissions.stream()
                .anyMatch( ( profilePermission ) -> Objects.equals( profilePermission.getPermissionName(),
                        PermissionList.RESERVATION.name() ) && profilePermission.getValueRead() == 1 );


        boolean hasSameId = Objects.equals( employeeId, user.getId() );
        //Ritorna TRUE SE L'ID di Project è lo stesso tra il richiedente e l'employee richiesto
        boolean hasSameProject = Objects.equals( employeeService.getEmployeeById( employeeId ).getProject().getId(), employeeService.getEmployeeById( user.getId() ).getProject().getId() );

        if( hasSameId || hasSameProject || hasReservationReadPermission ) {
            Specification<Reservation> spec = ReservationSpec.findByEmployeeAndDate( employeeId, calendarId );
            List<Reservation> reservations = reservationRepository.findAll( spec );

            return reservations.stream().map( this::mapEntityToReservationDTO ).toList();
        } else throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_PERMISSION );
    }

    @Override
    public List<ReservationDTO> getAllByMonthAndYearAndEmployeeIdOrderByDayAsc( int month, int year, Long employeeId ) {
        return reservationRepository.findByCalendarMonthAndCalendarYearAndEmployeeUserIdOrderByCalendarDayAsc( month, year, employeeId )
                .stream().map( this::mapEntityToReservationDTO ).toList();
    }

    @Override
    public List<ReservationDTO> getAllByMonthAndYear( int month, int year ) {
        return reservationRepository.findByCalendarMonthAndCalendarYear( month, year )
                .stream().map( this::mapEntityToReservationDTO ).toList();
    }


    public ReservationDTO mapEntityToReservationDTO( Reservation reservation ) {
        ReservationDTO reservationDTO = new ReservationDTO();

        reservationDTO.setId( reservation.getId() );

        reservationDTO.setOfficeId( reservation.getOffice() != null ? reservation.getOffice().getId() : null );

        reservationDTO.setEmployeeId( reservation.getEmployee().getUserId() );

        reservationDTO.setCalendarId( reservation.getCalendar().getId() );

        reservationDTO.setClientId( reservation.getClient() != null ? reservation.getClient().getId() : null );

        reservationDTO.setSmart( reservation.isSmart() );

        reservationDTO.setReservationTypeName( reservation.getReservationType().name() );

        return reservationDTO;
    }


    //Metodo di controllo per verificare se l'utente può effettuare una prenotazione
    //controllando se l'id di chi sta prenotando è uguale a quello di chi sta richiedendo
    boolean canManageReservation( Long employeeId ) {
        //RECUPERO L'UTENTE DELLA RICHIESTA
        UserDTOInternal user = userService.getUserByAuthentication();

        //CONTROLLO SE HA I PERMESSI DI CREATE
        boolean hasPermission = hasUserReservationCreatePermission();
        //CONTROLLO SE HA LO STESSO ID DELL'UTENTE CHE VUOLE CREARE
        boolean hasSameId = Objects.equals( employeeId, user.getId() );

        //SE E' UN DIPENDENTE
        if( hasSameId && !hasPermission ) {
            Employee employeeFound = employeeService.getEmployeeById( employeeId );
            //SE E' BLOCCATO LANCIA ECCEZIONE
            if( employeeFound.getBlockedTo() != null ) {
                if( employeeFound.getBlockedTo().isAfter( LocalDate.now() ) ) {
                    throw new appException( HttpStatus.BAD_REQUEST, "EMPLOYEE " + ErrorCodeList.IS_BLOCKED );
                }
            }

        }
        return hasSameId || hasPermission;

    }

    //METODO CHE CONTROLLA SE L'UTENTE PUO' VISUALIZZARE IL PROGETTO(SE NON E' ADMIN CONTROLLA SE APPARTIENE AL PROGETTO)
    boolean canViewProjectReservation( Long projectId ) {
        UserDTOInternal user = userService.getUserByAuthentication();

        ProfileDTO profile = userService.getProfile( user.getId() );

        Set<ProfilePermissionDTO> profilePermissions = userService.getProfilePermissions( profile.getUserId() );

        return Objects.equals( employeeService.getEmployeeById( user.getId() ).getProject().getId(), projectId ) ||
                profilePermissions
                        .stream()
                        .anyMatch( ( profilePermission ) -> Objects.equals
                        ( profilePermission.getPermissionName(), PermissionList.RESERVATION.name() )
                        && profilePermission.getValueRead() == 1 );
    }

    //METODO CHE CONTROLLA SE L'UTENTE PUO' ASSEGNARE L'UFFICIO O IL CLIENT(DEVE AVERE RESERVATION_CREATE)
    private boolean hasUserReservationCreatePermission() {
        UserDTOInternal user = userService.getUserByAuthentication();

        ProfileDTO profile = userService.getProfile( user.getId() );

        Set<ProfilePermissionDTO> profilePermissions = userService.getProfilePermissions( profile.getUserId() );

        return profilePermissions.stream()
                .anyMatch( ( profilePermission ) -> Objects.equals
                        ( profilePermission.getPermissionName(), PermissionList.RESERVATION.name() )
                        && profilePermission.getValueCreate() == 1 );
    }

    // VERIFICA SE IL TIPO DI PRENOTAZIONE DI RESERVATION E' UNO DEI TIPI DI PRENOTAZIONE VALIDI
    boolean isCorrectReservationType( ReservationDTO reservation ) {
        ReservationType[] reservationTypes = ReservationType.values();

        return Arrays.stream( reservationTypes ).anyMatch( ( type ) -> Objects.equals( type.name(), reservation.getReservationTypeName() ) );
    }


    //QUESTO METODO SETTA IL CALENDAR IN MODO CHE SE L'UTENTE NON HA I PERMESSI CREATE PUO' SOLO IMPOSTARE IL GIORNO DELLA PRENOTAZIONE,
    //IL MESE E L'ANNO COME: MESE ATTUALE+1 E ANNO ATTUALE ENTRO IL 15 DEL MESE ODIERNO
    private void setCalendarByAuthentication( Reservation reservation, Calendar calendar ) {
        //SE L'UTENTE HA I PERMESSI DI CREATE SETTA IL CALENDAR EVITANDO TUTTI I CONTROLLI
        if( hasUserReservationCreatePermission() ) {
            reservation.setCalendar( calendar );
        } else {
            int monthOfReservation = LocalDate.now().getMonthValue() + 1;
            int yearOfReservation = LocalDate.now().getYear();
            //PRENDO LA DATA ODIERNA
            //todo da scommentare in fase di rilascio
//            LocalDate today = LocalDate.now();
//            //PRENDO LA DATA DEL 15 DEL MESE ODIERNO
//            LocalDate fifteenthOfTodayMonth = today.withDayOfMonth(15);
//            //SE LA DATA ODIERNA E' PRECEDENTE AL 15 DEL MESE RITORNA TRUE
//            boolean isInTimeToCreate = today.isBefore(fifteenthOfTodayMonth);
//            //LANCIA ECCEZIONE SE LA DATA ODIERNA E' SUCCESSIVA AL 15 DEL MESE ODIERNO
//            if (!isInTimeToCreate) {
//                throw new SmartAxcentException(HttpStatus.BAD_REQUEST, EntityErrorCodeList.RESERVATION_TIME_EXPIRED);
//            }
            //LANCIA ECCEZIONE SE LA DATA DI PRENOTAZIONE NON E' DEL MESE SUCCESSIVO E DELL'ANNO CORRENTE
            if( monthOfReservation != calendar.getMonth() || yearOfReservation != calendar.getYear() ) {
                throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.INVALID_RESERVATION );
            }
            reservation.setCalendar( calendar );

        }
    }

}
