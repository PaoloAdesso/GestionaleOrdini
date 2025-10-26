package it.paoloadesso.gestionaleordini.exceptionhandling;

import it.paoloadesso.gestionaleordini.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntitaGiaEsistenteException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntitaGiaEsistente(EntitaGiaEsistenteException ex) {
        log.warn("Entità già esistente: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                "409",
                "ENTITA_GIA_ESISTENTE"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EntitaNonTrovataException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntitaNonTrovata(EntitaNonTrovataException ex) {
        log.warn("Entità non trovata: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                "404",
                "ENTITA_NON_TROVATA"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(StatoNonValidoException.class)
    public ResponseEntity<ErrorResponseDTO> handleStatoNonValido(StatoNonValidoException ex) {
        log.warn("Stato non valido: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                "409",
                "STATO_NON_VALIDO"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ModificaVuotaException.class)
    public ResponseEntity<ErrorResponseDTO> handleModificaVuota(ModificaVuotaException ex) {
        log.warn("Modifica vuota rilevata: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getMessage(),
                "400",
                "MODIFICA_VUOTA"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Cattura gli errori RuntimeException, inclusi quelli lanciati nei miei Service.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException catturata: {}", e.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                e.getMessage(),
                "500",
                "ERRORE_BUSINESS"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura gli errori che lancio io nei Service con ResponseStatusException.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatus(ResponseStatusException ex) {
        log.warn("ResponseStatusException catturata: {} - Status: {}", ex.getReason(), ex.getStatusCode());

        String codiceErroreNumerico = ex.getStatusCode().toString();
        String messaggio = ex.getReason();
        String codiceErrore = "ERRORE_BUSINESS";

        if (messaggio != null && messaggio.contains(":")) {
            String[] parts = messaggio.split(":", 2);
            codiceErrore = parts[0].trim();
            messaggio = parts[1].trim();
        }

        ErrorResponseDTO error = new ErrorResponseDTO(messaggio, codiceErroreNumerico, codiceErrore);
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    /**
     * Cattura errori di validazione sui DTO, quando @Valid fallisce.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Errori di validazione DTO: {} campi non validi", ex.getBindingResult().getFieldErrors().size());
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Cattura errori di validazione sui parametri URL.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Violazione constraint sui parametri: {}", ex.getMessage());
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Errore di validazione sui parametri",
                "400",
                "VALIDAZIONE_PARAMETRI"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura quando manca un parametro obbligatorio nelle richieste.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("Parametro mancante nella richiesta: {}", ex.getParameterName());
        String message = getMissingParameterMessage(ex.getParameterName());

        ErrorResponseDTO error = new ErrorResponseDTO(
                message,
                "400",
                "PARAMETRO_MANCANTE"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura quando il tipo del parametro è sbagliato.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Tipo parametro errato: {} - valore ricevuto: {}", ex.getName(), ex.getValue());
        String message = getTypeMismatchMessage(ex.getName());

        ErrorResponseDTO error = new ErrorResponseDTO(
                message,
                "400",
                "TIPO_PARAMETRO_ERRATO"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura tutti gli errori imprevisti che non ho gestito.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericErrors(Exception ex) {
        log.error("Errore generico non gestito: {} - Classe: {}", ex.getMessage(), ex.getClass().getSimpleName());
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Si è verificato un errore interno del server",
                "500",
                "ERRORE_INTERNO"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getMissingParameterMessage(String parameterName) {
        return switch (parameterName) {
            case "idTavolo" -> "L'id del tavolo è obbligatorio";
            case "idOrdine" -> "L'id dell'ordine è obbligatorio";
            case "idProdotto" -> "L'id del prodotto è obbligatorio";
            case "categoria" -> "La categoria è obbligatoria";
            default -> "Il parametro '" + parameterName + "' è obbligatorio";
        };
    }

    /**
     * Metodo di aiuto: quando il tipo del parametro è sbagliato, questo metodo decide quale messaggio mostrare.
     * Per esempio: se mando "abc" invece di un numero per idTavolo.
     */
    private String getTypeMismatchMessage(String parameterName) {
        return switch (parameterName) {
            case "idTavolo" -> "L'id del tavolo deve essere un numero positivo";
            case "idOrdine" -> "L'id dell'ordine deve essere un numero positivo";
            case "idProdotto" -> "L'id del prodotto deve essere un numero positivo";
            default -> "Il parametro '" + parameterName + "' ha un formato non valido";
        };
    }
}
