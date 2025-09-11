package it.paoloadesso.gestioneordini.entities;

import it.paoloadesso.gestioneordini.enums.StatoTavolo;
import it.paoloadesso.gestioneordini.enums.converters.StatoOrdineConverter;
import it.paoloadesso.gestioneordini.enums.converters.StatoTavoloConverter;
import jakarta.persistence.*;


@Entity
@Table(name = "tavoli")
public class TavoliEntity {

    @Id
    @SequenceGenerator(name = "tavoli_id_gen", sequenceName = "tavoli_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tavoli_id_gen")
    private Long id;

    @Column(name = "numero_nome_tavolo", nullable = false)
    private String numeroNomeTavolo;


    @Convert(converter = StatoTavoloConverter.class)
    @Column(name = "stato", nullable = false)
    private StatoTavolo statoTavolo;

    public TavoliEntity(Long id, String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.id = id;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public TavoliEntity() {
        this.statoTavolo = StatoTavolo.LIBERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public StatoTavolo getStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(StatoTavolo statoTavolo) {
        this.statoTavolo = statoTavolo;
    }
}
