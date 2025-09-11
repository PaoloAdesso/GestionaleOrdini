package it.paoloadesso.gestioneordini.entities;

import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "ordini")
public class OrdiniEntity {

    @Id
    @SequenceGenerator(name = "ordini_id_gen", sequenceName = "ordini_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ordini_id_gen")
    private Long idOrdine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tavolo", nullable = false)
    private TavoliEntity idTavolo;

    @Column(name = "data_ordine", nullable = false)
    private LocalDate dataOrdine;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_ordine", nullable = false)
    private StatoOrdine statoOrdine;

    public OrdiniEntity() {
        this.statoOrdine = StatoOrdine.IN_ATTESA;
    }

    public OrdiniEntity(Long idOrdine, TavoliEntity idTavolo, LocalDate dataOrdine, StatoOrdine statoOrdine) {
        this.idOrdine = idOrdine;
        this.idTavolo = idTavolo;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public TavoliEntity getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(TavoliEntity idTavolo) {
        this.idTavolo = idTavolo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }
}
