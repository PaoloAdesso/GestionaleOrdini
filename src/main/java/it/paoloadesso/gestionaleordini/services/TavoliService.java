package it.paoloadesso.gestionaleordini.services;

import it.paoloadesso.gestionaleordini.dto.AggiornaTavoloDTO;
import it.paoloadesso.gestionaleordini.dto.CreaTavoliRequestDTO;
import it.paoloadesso.gestionaleordini.dto.TavoliResponseDTO;
import it.paoloadesso.gestionaleordini.entities.TavoliEntity;
import it.paoloadesso.gestionaleordini.enums.StatoTavolo;
import it.paoloadesso.gestionaleordini.exceptionhandling.EntitaGiaEsistenteException;
import it.paoloadesso.gestionaleordini.exceptionhandling.EntitaNonTrovataException;
import it.paoloadesso.gestionaleordini.mapper.TavoliMapper;
import it.paoloadesso.gestionaleordini.repositories.TavoliRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TavoliService {

    private static final Logger log = LoggerFactory.getLogger(TavoliService.class);

    private final TavoliRepository tavoliRepository;
    private final TavoliMapper tavoliMapper;

    public TavoliService(TavoliRepository tavoliRepository, TavoliMapper tavoliMapper) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliMapper = tavoliMapper;
    }

    @Transactional
    public TavoliResponseDTO creaTavolo(CreaTavoliRequestDTO dto) {
        log.info("Tentativo di creazione tavolo: {}", dto.getNumeroNomeTavolo());

        if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCase(dto.getNumeroNomeTavolo())) {
            log.warn("Tentativo di creare un tavolo già esistente: {}", dto.getNumeroNomeTavolo());
            throw new EntitaGiaEsistenteException("tavolo", "numero/nome «" + dto.getNumeroNomeTavolo() + "»");
        }

        TavoliEntity tavolo = tavoliRepository.save(tavoliMapper.createTavoliDtoToEntity(dto));
        log.info("Tavolo creato con successo - ID: {}, Nome: {}", tavolo.getId(), tavolo.getNumeroNomeTavolo());

        return tavoliMapper.entityToDto(tavolo);
    }

    public List<TavoliResponseDTO> getTavoli() {
        log.debug("Richiesta lista di tutti i tavoli");
        List<TavoliEntity> listaTavoli = tavoliRepository.findAll();
        log.info("Trovati {} tavoli", listaTavoli.size());

        return listaTavoli.stream()
                .map(tavoliMapper::entityToDto)
                .toList();
    }

    public List<TavoliResponseDTO> getTavoliLiberi() {
        log.debug("Richiesta lista tavoli liberi");
        List<TavoliEntity> listaTavoli = tavoliRepository.findByStatoTavolo(StatoTavolo.LIBERO);
        log.info("Trovati {} tavoli liberi", listaTavoli.size());

        return listaTavoli.stream()
                .map(tavoliMapper::entityToDto)
                .toList();
    }

    @Transactional
    public TavoliResponseDTO aggiornaTavolo(Long id, AggiornaTavoloDTO dto) {
        log.info("Tentativo di aggiornamento tavolo con ID: {}", id);

        // Verifico che il tavolo esista
        TavoliEntity tavoloEsistente = tavoliRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tavolo con ID {} non trovato per aggiornamento", id);
                    return new EntitaNonTrovataException("Tavolo", id);
                });

        // Aggiorno solo i campi forniti (non-null)
        if (dto.getNumeroNomeTavolo() != null) {
            log.debug("Aggiornamento nome tavolo ID {}: '{}' -> '{}'",
                    id, tavoloEsistente.getNumeroNomeTavolo(), dto.getNumeroNomeTavolo());

            // Verifico che il nome non sia già usato da un altro tavolo
            if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCaseAndIdNot(
                    dto.getNumeroNomeTavolo(), id)) {
                log.warn("Tentativo di assegnare nome già esistente '{}' al tavolo ID {}",
                        dto.getNumeroNomeTavolo(), id);
                throw new EntitaGiaEsistenteException("tavolo", "nome «" + dto.getNumeroNomeTavolo() + "»");
            }
            tavoloEsistente.setNumeroNomeTavolo(dto.getNumeroNomeTavolo());
        }

        if (dto.getStatoTavolo() != null) {
            log.debug("Aggiornamento stato tavolo ID {}: {} -> {}",
                    id, tavoloEsistente.getStatoTavolo(), dto.getStatoTavolo());
            tavoloEsistente.setStatoTavolo(dto.getStatoTavolo());
        }

        TavoliEntity tavoloAggiornato = tavoliRepository.save(tavoloEsistente);
        log.info("Tavolo ID {} aggiornato con successo", id);

        return tavoliMapper.entityToDto(tavoloAggiornato);
    }
}
