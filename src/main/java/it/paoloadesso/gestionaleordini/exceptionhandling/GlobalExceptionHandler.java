package it.paoloadesso.gestionaleordini.exceptionhandling;

import it.paoloadesso.gestionaleordini.dto.ErrorResponseDTO;
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

    // Metto queste stringhe in costanti così se devo cambiarle lo faccio una volta sola
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    /**
     * Questo metodo cattura gli errori che lancio io nei Service.
     * Ad esempio: throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato")
     * Prende il messaggio che ho scritto e lo restituisce in formato JSON.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessErrors(ResponseStatusException ex) {
        // Creo la mappa che diventerà il JSON di risposta
        Map<String, Object> response = new HashMap<>();
        response.put(TIMESTAMP, LocalDateTime.now());
        response.put(STATUS, ex.getStatusCode().value());
        response.put(ERROR, ex.getStatusCode().toString());
        response.put(MESSAGE, ex.getReason()); // Questo è il messaggio che ho scritto nel Service

        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    /**
     * Questo metodo cattura 4 tipi diversi di errori di validazione.
     * Uso instanceof per capire che tipo di errore è e comportarmi di conseguenza.
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,    // Errori sui DTO quando @Valid fallisce
            ConstraintViolationException.class,       // Errori sui parametri URL quando @Positive fallisce
            MissingServletRequestParameterException.class, // Quando manca un parametro obbligatorio
            MethodArgumentTypeMismatchException.class      // Quando il tipo è sbagliato, ad esempio: testo invece di numero
    })
    public ResponseEntity<?> handleValidationErrors(Exception ex) {

        // Caso speciale: per gli errori sui DTO restituisco una mappa campo→errore
        if (ex instanceof MethodArgumentNotValidException validationEx) {
            Map<String, String> errors = new HashMap<>();
            // Per ogni campo sbagliato nel DTO, metto: nomeCampo → messaggioErrore
            validationEx.getBindingResult().getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // Per tutti gli altri errori uso il formato standard
        Map<String, Object> response = createStandardErrorResponse();

        // Controllo che tipo di errore è e ci metto il messaggio giusto
        if (ex instanceof ConstraintViolationException) {
            response.put(MESSAGE, "Errore di validazione sui parametri");

        } else if (ex instanceof MissingServletRequestParameterException missingParamEx) {
            // Parametro mancante: uso il messaggio specifico che ho creato nel metodo d'aiuto
            String message = getMissingParameterMessage(missingParamEx.getParameterName());
            response.put(MESSAGE, message);

        } else if (ex instanceof MethodArgumentTypeMismatchException typeMismatchEx) {
            // Tipo sbagliato: uso il messaggio specifico che ho creato nel metodo d'aiuto
            String message = getTypeMismatchMessage(typeMismatchEx.getName());
            response.put(MESSAGE, message);
        }

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Questo metodo cattura tutti gli errori imprevisti che non ho gestito.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericErrors(Exception ex) {
        Map<String, Object> response = createStandardErrorResponse();
        response.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.toString());
        response.put(MESSAGE, "Si è verificato un errore interno del server");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrdineNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrdineNotFound(OrdineNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(ex.getMessage(), "ORDINE_NON_TROVATO"));
    }

    @ExceptionHandler(OrdineChiusoException.class)
    public ResponseEntity<ErrorResponseDTO> handleOrdineChiuso(OrdineChiusoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDTO(ex.getMessage(), "ORDINE_GIA_CHIUSO"));
    }

    @ExceptionHandler(ProdottiNonPagatiException.class)
    public ResponseEntity<ErrorResponseDTO> handleProdottiNonPagati(ProdottiNonPagatiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage(), "PRODOTTI_NON_PAGATI"));
    }

    @ExceptionHandler(ModificaStatoNonPermessaException.class)
    public ResponseEntity<ErrorResponseDTO> handleModificaStatoNonPermessa(ModificaStatoNonPermessaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(ex.getMessage(), "MODIFICA_STATO_NON_PERMESSA"));
    }

    /**
     * Metodo di aiuto: crea la struttura base della risposta di errore.
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
     * Metodo di aiuto: quando manca un parametro, questo metodo decide quale messaggio mostrare.
     * Ho messo messaggi specifici per i parametri che uso più spesso.
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
