package it.paoloadesso.gestioneordini.services;

import it.paoloadesso.gestioneordini.dto.CreaTavoliDto;
import it.paoloadesso.gestioneordini.dto.AggiornaTavoliRequestDto;
import it.paoloadesso.gestioneordini.dto.TavoliResponseDto;
import it.paoloadesso.gestioneordini.entities.TavoliEntity;
import it.paoloadesso.gestioneordini.mapper.TavoliCreateMapper;
import it.paoloadesso.gestioneordini.mapper.TavoliMapper;
import it.paoloadesso.gestioneordini.repositories.TavoliRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TavoliService {

    private final TavoliRepository tavoliRepository;
    private final TavoliCreateMapper tavoliCreateMapper;
    private final TavoliMapper tavoliMapper;


    public TavoliService(TavoliRepository tavoliRepository, TavoliCreateMapper tavoliCreateMapper, TavoliMapper tavoliMapper) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliCreateMapper = tavoliCreateMapper;
        this.tavoliMapper = tavoliMapper;
    }

    public TavoliResponseDto creaTavolo(CreaTavoliDto dto) {
        if (tavoliRepository.existsByNumeroNomeTavolo(dto.getNumeroNomeTavolo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tavolo non creato poiché il nome del tavolo esiste già.");
        }

        TavoliEntity entity = tavoliRepository.save(tavoliCreateMapper.dtoToEntity(dto));
        return tavoliMapper.entityToDto(entity);
    }


    public TavoliResponseDto aggiornaTavolo(AggiornaTavoliRequestDto dtoTavolo) {
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
}
