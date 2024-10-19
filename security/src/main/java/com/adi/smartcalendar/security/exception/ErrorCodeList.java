package com.adi.smartcalendar.security.exception;

import lombok.Getter;

@Getter
public class ErrorCodeList {
    //RISORSA NON TROVATA
    public static final String NF404 = "NF404";

    //USERNAME O PASS O EMAIL ERRATE
    public static final String BADCREDENTIALS = "BADCREDENTIALS";

    // AL MOMENTO DELLA REGISTRAZIONE SE L'UTENTE NON E' ABILITATO
    public static final String NOTUSERENABLED = "NOTUSERENABLED";

    // AL CAMBIO PASSWORD TUTTI I VECCHI TOKEN DIVENTANO OBSOLETI
    public static final String TOKENOBSOLETE = "TOKENOBSOLETE";

    // ACCESSO NEGATO
    public static final String ACCESSDENIED = "ACCESSDENIED";

    // TOKEN NON VALIDO
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
}
