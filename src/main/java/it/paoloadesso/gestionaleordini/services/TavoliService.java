package it.paoloadesso.gestionaleordini.services;

import it.paoloadesso.gestionaleordini.dto.AggiornaTavoloDTO;
import it.paoloadesso.gestionaleordini.dto.CreaTavoliRequestDTO;
import it.paoloadesso.gestionaleordini.dto.TavoliResponseDTO;
import it.paoloadesso.gestionaleordini.entities.TavoliEntity;
import it.paoloadesso.gestionaleordini.enums.StatoTavolo;
import it.paoloadesso.gestionaleordini.mapper.TavoliMapper;
import it.paoloadesso.gestionaleordini.repositories.TavoliRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TavoliService {

    private final TavoliRepository tavoliRepository;
    private final TavoliMapper tavoliMapper;


    public TavoliService(TavoliRepository tavoliRepository, TavoliMapper tavoliMapper) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliMapper = tavoliMapper;
    }

    @Transactional
    public TavoliResponseDTO creaTavolo(CreaTavoliRequestDTO dto) {
        if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCase(dto.getNumeroNomeTavolo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esiste già un tavolo con numero/nome: " + dto.getNumeroNomeTavolo()
            );
        }

        TavoliEntity tavolo = tavoliRepository.save(tavoliMapper.createTavoliDtoToEntity(dto));
        return tavoliMapper.entityToDto(tavolo);
    }

    public List<TavoliResponseDTO> getTavoli() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findAll();
        List<TavoliResponseDTO> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el -> tavoliMapper.entityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    public List<TavoliResponseDTO> getTavoliLiberi() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findByStatoTavolo(StatoTavolo.LIBERO);
        List<TavoliResponseDTO> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el -> tavoliMapper.entityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    @Transactional
    public TavoliResponseDTO aggiornaTavolo(Long id, AggiornaTavoloDTO dto) {
        // Verifico che il tavolo esista
        TavoliEntity tavoloEsistente = tavoliRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tavolo con ID " + id + " non trovato."));

        // Aggiorno solo i campi forniti (non-null)
        if (dto.getNumeroNomeTavolo() != null) {
            // Verifico che il nome non sia già usato da un altro tavolo
            if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCaseAndIdNot(
                    dto.getNumeroNomeTavolo(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Esiste già un altro tavolo con nome: " + dto.getNumeroNomeTavolo());
            }
            tavoloEsistente.setNumeroNomeTavolo(dto.getNumeroNomeTavolo());
        }

        if (dto.getStatoTavolo() != null) {
            tavoloEsistente.setStatoTavolo(dto.getStatoTavolo());
        }

        TavoliEntity tavoloAggiornato = tavoliRepository.save(tavoloEsistente);
        return tavoliMapper.entityToDto(tavoloAggiornato);
    }


}
