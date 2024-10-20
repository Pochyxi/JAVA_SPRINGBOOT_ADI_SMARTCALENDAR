package com.adi.smartcalendar.security.exception;

import lombok.Getter;

@Getter
public class ErrorCodeList {
    //RISORSA NON TROVATA
    public static final String NF404 = "NF404";

    //min = 6, max = 50
    public static final String SIZEERROR = "SIZEERROR";

    // AL MOMENTO DELLA REGISTRAZIONE SE L'UTENTE NON E' ABILITATO
    public static final String NOTUSERENABLED = "NOTUSERENABLED";


    // AL CAMBIO PASSWORD TUTTI I VECCHI TOKEN DIVENTANO OBSOLETI
    public static final String TOKENOBSOLETE = "TOKENOBSOLETE";

    // DEVE ESSERE PER FORZA TUTTO IN UPPERCASE
    public static final String UPPERCASEERROR = "UPPERCASEERROR";


    public static final String CANNOT_BE_EMPTY = "CANNOT_BE_EMPTY";

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";

    public static final String NOT_EMPTY_PROJECT = "NOT_EMPTY_PROJECT";

    public static final String EXISTING_RESERVATION = "EXISTING_RESERVATION";

    public static final String INVALID_PERMISSION = "INVALID_PERMISSION";

    public static final String DATE_INVALID = "DATE_INVALID";

    public static final String AMBIGUOUS_FIELDS = "AMBIGUOUS_FIELD";

    public static final String NOT_CORRECT_TYPE = "NOT_CORRECT_TYPE";

    public static final String IS_BLOCKED = "IS_BLOCKED" ;

    public static final String IS_HOLIDAY = "IS_HOLIDAY" ;

    public static final String INVALID_RESERVATION = "INVALID_RESERVATION" ;

    public static final String ACCESSDENIED = "ACCESSDENIED";
}
