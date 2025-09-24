package it.paoloadesso.gestioneordini.exceptionhandling;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Ho messo queste parole in costanti così se le devo cambiare lo faccio una volta sola
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    /**
     * Questo metodo cattura gli errori che lancio io nei Service.
     * Per esempio: throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato")
     * Prende il messaggio che ho scritto io e lo manda all'utente in formato JSON.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessErrors(ResponseStatusException ex) {
        // Creo la mappa che diventerà il JSON di risposta
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, ex.getStatusCode().value());
        response.put(ERROR, ex.getStatusCode().toString());
        response.put(MESSAGE, ex.getReason()); // Il messaggio d'errore che ho scritto nel Service

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    /**
     * Questo metodo cattura 4 tipi di errori di validazione diversi.
     * Uso instanceof per capire di che tipo è l'errore e comportarmi di conseguenza.
     * È come avere un solo dottore che cura 4 malattie diverse.
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,    // Errori sui DTO (quando @Valid fallisce)
            ConstraintViolationException.class,       // Errori sui parametri URL (quando @Positive fallisce)
            MissingServletRequestParameterException.class, // Parametri mancanti
            MethodArgumentTypeMismatchException.class      // Tipi sbagliati (testo invece di numero)
    })
    public ResponseEntity<?> handleValidationErrors(Exception ex) {

        // Caso speciale: errori sui DTO restituiscono una mappa campo->errore
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            Map<String, String> errors = new HashMap<>();
            // Per ogni campo sbagliato nel DTO, metto campo->messaggio di errore
            validationEx.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // Per tutti gli altri errori uso il formato standard
        Map<String, Object> response = createStandardErrorResponse();

        // Controllo che tipo di errore è e metto il messaggio giusto
        if (ex instanceof ConstraintViolationException) {
            response.put(MESSAGE, "Errore di validazione sui parametri");

        } else if (ex instanceof MissingServletRequestParameterException missingParamEx) {
            // Parametro mancante: uso il messaggio specifico
            String message = getMissingParameterMessage(missingParamEx.getParameterName());
            response.put(MESSAGE, message);

        } else if (ex instanceof MethodArgumentTypeMismatchException typeMismatchEx) {
            // Tipo sbagliato: uso il messaggio specifico
            String message = getTypeMismatchMessage(typeMismatchEx.getName());
            response.put(MESSAGE, message);
        }

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Qui catturo tutti gli errori che non ho previsto.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericErrors(Exception ex) {
        Map<String, Object> response = createStandardErrorResponse();
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        response.put(MESSAGE, "Si è verificato un errore interno del server");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Metodo di aiuto: crea la base della risposta di errore.
     * Così non devo riscrivere sempre le stesse cose.
     */
    private Map<String, Object> createStandardErrorResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, HttpStatus.BAD_REQUEST.value());
        response.put(ERROR, HttpStatus.BAD_REQUEST.toString());
        return response;
    }

    /**
     * Quando manca un parametro, questo metodo decide che messaggio mostrare.
     * Ho messo i messaggi specifici per i parametri che uso spesso.
     */
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
     * Quando il tipo del parametro è sbagliato, questo metodo decide che messaggio mostrare.
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
