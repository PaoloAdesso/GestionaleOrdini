package it.paoloadesso.gestioneordini.services;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDto;
import it.paoloadesso.gestioneordini.dto.TavoliDto;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import it.paoloadesso.gestioneordini.mapper.TavoliMapper;
import it.paoloadesso.gestioneordini.repositories.TavoliRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TavoliService {

    private final TavoliRepository tavoliRepository;
    private final TavoliMapper tavoliMapper;


    public TavoliService(TavoliRepository tavoliRepository, TavoliMapper tavoliMapper) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliMapper = tavoliMapper;
    }

    public TavoliDto creaTavolo(CreaTavoliDto dto) {
        if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCase(dto.getNumeroNomeTavolo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tavolo non creato poiché il nome del tavolo esiste già.");
        }

        TavoliEntity tavolo = tavoliRepository.save(tavoliMapper.createTavoliDtoToEntity(dto));
        return tavoliMapper.entityToDto(tavolo);
    }


    public TavoliDto aggiornaTavolo(TavoliDto dtoTavolo) {
        // Verifico se l'id del tavolo esiste
        TavoliEntity tavolo = tavoliRepository.findById(dtoTavolo.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tavolo con ID " + dtoTavolo.getId() + " non trovato."));

        // Aggiorno i campi dell'entity con quelli del DTO
        tavolo.setNumeroNomeTavolo(dtoTavolo.getNumeroNomeTavolo());
        tavolo.setStatoTavolo(dtoTavolo.getStatoTavolo());
        // Salvo l'entity aggiornata
        TavoliEntity tavoloAggiornato = tavoliRepository.save(tavolo);

        // Mappo l'entity salvata a DTO e lo restituisco
        return tavoliMapper.entityToDto(tavoloAggiornato);
    }

    public List<TavoliDto> getTavoli() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findAll();
        List<TavoliDto> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el->tavoliMapper.entityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    public void deleteTavoloById(Long idTavolo) {
        // Cerco il tavolo e se non c'è restituisco relativo messaggio
        TavoliEntity tavolo = tavoliRepository.findById(idTavolo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tavolo con ID " + idTavolo + " non trovato."));
        // Elimino il tavolo trovato
        tavoliRepository.delete(tavolo);
    }

    public List<TavoliDto> getTavoliLiberi() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findByStatoTavolo(StatoTavolo.LIBERO);
        List<TavoliDto> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el->tavoliMapper.entityToDto(el))
                .toList();
        return tavoliResponseDto;
    }
}
