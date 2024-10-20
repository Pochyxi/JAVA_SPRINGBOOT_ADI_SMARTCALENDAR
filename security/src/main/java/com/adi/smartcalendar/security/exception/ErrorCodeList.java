package com.adi.smartcalendar.security.exception;

import lombok.Getter;

@Getter
public class ErrorCodeList {
    //RISORSA NON TROVATA
    public static final String NF404 = "NF404";

    //min = 6, max = 50
    public static final String SIZEERROR = "SIZEERROR";

    // @Mail
    public static final String FORMATERROR = "FORMATERROR";

    //USERNAME O PASS O EMAIL ERRATE
    public static final String BADCREDENTIALS = "BADCREDENTIALS";

    //Al MOMENTO DELLA REGISTRAZIONE SE QUESTO USERNAME E' GIA UTILIZZATO
    public static final String  EXISTINGUSERNAME = "EXISTINGUSERNAME";

    //AL MOMENTO DELLA REGISTRAZIONE SE QUESTA MAIL E' GIA UTILIZZATA
    public static final String EXISTINGEMAIL = "EXISTINGEMAIL";

    // AL MOMENTO DELLA REGISTRAZIONE SE LE PASS NON CORRISPONDONO
    public static final String NOTSAMEPASSWORDS = "NOTSAMEPASSWORDS";

    // AL MOMENTO DELLA REGISTRAZIONE SE L'UTENTE NON E' ABILITATO
    public static final String NOTUSERENABLED = "NOTUSERENABLED";

    // AL MOMENTO DELLA LOGIN SE LA PASS E' TEMPORANEA
    public static final String TEMPORARYPASSWORD = "TEMPORARYPASSWORD";

    // AL CAMBIO PASSWORD TUTTI I VECCHI TOKEN DIVENTANO OBSOLETI
    public static final String TOKENOBSOLETE = "TOKENOBSOLETE";

    // NON SI PUO' AVERE PIU' DI UNA CONFIMATION PER UN UTENTE
    public static final String EXISTINGCONFIRMATION = "EXISTINGCONFIRMATION";

    // DEVE ESSERE PER FORZA TUTTO IN UPPERCASE
    public static final String UPPERCASEERROR = "UPPERCASEERROR";

    // TOKEN NON VALIDO
    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    public static final String CANNOT_BE_EMPTY = "CANNOT_BE_EMPTY";

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";

    public static final String NOT_EMPTY_PROJECT = "NOT_EMPTY_PROJECT";

    public static final String NO_SIT_IN_OFFICE = "NO_SIT_IN_OFFICE";

    public static final String EXISTING_RESERVATION = "EXISTING_RESERVATION";

    public static final String INVALID_PERMISSION = "INVALID_PERMISSION";

    public static final String UPDATE_NOT_CORRECT = "UPDATE_NOT_CORRECT";

    public static final String DATE_INVALID = "DATE_INVALID";

    public static final String AMBIGUOUS_FIELDS = "AMBIGUOUS_FIELD";

    public static final String NOT_CORRECT_TYPE = "NOT_CORRECT_TYPE";


    public static final String RESERVATION_TIME_EXPIRED = "RESERVATION_TIME_EXPIRED" ;

    public static final String IS_BLOCKED = "IS_BLOCKED" ;

    public static final String IS_HOLIDAY = "IS_HOLIDAY" ;

    public static final String INVALID_RESERVATION = "INVALID_RESERVATION" ;
}
