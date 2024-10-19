package com.adi.smartcalendar.security.service.impl;


import com.adi.smartcalendar.security.dto.*;
import com.adi.smartcalendar.security.exception.ErrorCodeList;
import com.adi.smartcalendar.security.exception.appException;
import com.adi.smartcalendar.security.service.UserApiService;
import com.adi.smartcalendar.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserApiService userApiService;

    @Override
    public UserDTO findById( Long id ) {
        return userApiService.getById( id ).block();
    }

    @Override
    public ProfileDTO getProfile( Long userId ) {
        return userApiService.getProfile( userId ).block();
    }

    /**
     * Ritorna un utente in base all'username o email
     * @param usernameOrEmail username o email
     * @return utente interno
     */
    @Override
    public Optional<UserDTOInternal> findByUsernameOrEmail( String usernameOrEmail) {
        return userApiService.getUserData(usernameOrEmail)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .block();
    }

    /**
     * Verifica se esiste un utente in base all'username o email
     * @param usernameOrEmail username o email
     * @return true se esiste, false altrimenti
     */
    @Override
    public boolean existsByUsernameOrEmail( String usernameOrEmail ) {
        return Boolean.TRUE.equals( userApiService.existsByUsernameOrEmail( usernameOrEmail ).block() );
    }

    /**
     * Ritorna i permessi di un profilo
     * @param profileId id del profilo
     * @return set di permessi
     */
    @Override
    public Set<ProfilePermissionDTO> getProfilePermissions( Long profileId ) {
        return userApiService.getProfilePermissions( profileId ).block();
    }

    /**
     * Ritorna tutti gli utenti
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy ordinamento
     * @param sortDir direzione dell'ordinamento
     * @return lista di utenti
     */
    @Override
    public PagedResponseDTO<UserDTO> getAllUsers( int pageNo, int pageSize, String sortBy, String sortDir) {
        int power = getPowerOfAuthenticatedUser();

        return userApiService.getAllUsers(pageNo, pageSize, sortBy, sortDir, power).block();
    }

    /**
     * Ritorna tutti gli utenti con email che contiene la stringa passata
     * @param email stringa da cercare
     * @param pageNo numero di pagina
     * @param pageSize dimensione della pagina
     * @param sortBy ordinamento
     * @param sortDir direzione dell'ordinamento
     * @return lista di utenti
     */
    @Override
    public PagedResponseDTO<UserDTO> getUsersByEmailContainsIgnoreCase( String email, int pageNo, int pageSize, String sortBy, String sortDir) {
        PagedResponseDTO<UserDTO> pagedUsers = userApiService.getUsersByEmailContainsIgnoreCase( email, pageNo, pageSize, sortBy, sortDir ).block();

        int power = getPowerOfAuthenticatedUser();

        return filterUsersByPower( pagedUsers, power );
    }

    @Override
    public UserDTO modifyUser( Long id, UserDTO userDTO ) {
        return userApiService.modifyUser( id, userDTO ).block();
    }

    @Override
    public void deleteUser( Long id ) {
        userApiService.deleteUser( id ).block();
    }

    /* GET USER BY AUTHENTICATION
     * Questo metodo recupera l'utente autenticato dal database.
     */
    @Override
    public UserDTOInternal getUserByAuthentication() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return findByUsernameOrEmail( email )
                .orElseThrow( () -> new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.NF404 ) );
    }

    private UserDTO mapUserDTOInternalToUserDTO( UserDTOInternal user ) {
        return UserDTO.builder()
                .id( user.getId() )
                .email( user.getEmail() )
                .username( user.getUsername() )
                .isEnabled( user.isEnabled() )
                .dateTokenCheck( user.getDateTokenCheck() )
                .isTemporaryPassword( user.isTemporaryPassword() )
                .profileName( user.getProfileName() )
                .profilePermissions( user.getProfilePermissions() )
                .build();
    }


    private PagedResponseDTO<UserDTO> filterUsersByPower( PagedResponseDTO<UserDTO> pagedUsers, int power ) {
        if( pagedUsers == null ) {
            throw new appException( HttpStatus.BAD_REQUEST, ErrorCodeList.NF404 );
        }
        List<UserDTO> users = pagedUsers.getContent();
        List<UserDTO> usersGreater = users.stream()
                .filter( u -> {
                    ProfileDTO profileDTO = getProfile( u.getId() );
                    return profileDTO.getPower() >= power;
                } )
                .toList();

        PagedResponseDTO<UserDTO> pagedResponseDTO = new PagedResponseDTO<>();
        pagedResponseDTO.setPageNo( pagedUsers.getPageNo() );
        pagedResponseDTO.setPageSize( pagedUsers.getPageSize() );
        pagedResponseDTO.setTotalElements( pagedUsers.getTotalElements() );
        pagedResponseDTO.setTotalPages( pagedUsers.getTotalPages() );
        pagedResponseDTO.setLast( pagedUsers.isLast() );
        pagedResponseDTO.setContent( usersGreater );

        return pagedResponseDTO;
    }

    private int getPowerOfAuthenticatedUser() {
        UserDTOInternal user = getUserByAuthentication();
        ProfileDTO profile = getProfile( user.getId() );

        return profile.getPower();
    }

    private String getEmailOfAuthenticatedUser() {
        UserDTOInternal user = getUserByAuthentication();
        return user.getEmail();
    }
}
