package it.paoloadesso.gestioneordini.entities;

import jakarta.persistence.*;


@Entity
@Table(name = "tavoli")
public class Tavoli {

    @Id
    @SequenceGenerator(name = "tavoli_id_gen", sequenceName = "tavoli_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tavoli_id_gen")
    private Long id;

    @Column(nullable = false)
    private String numeroNomeTavolo;


    //TODO boolean? devo usare ENUM
    @Column(nullable = false)
    private boolean statoTavolo;

    public Tavoli(Long id, String numeroNomeTavolo, boolean statoTavolo) {
        this.id = id;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public Tavoli() {

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

    public boolean isStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(boolean statoTavolo) {
        this.statoTavolo = statoTavolo;
    }
}
