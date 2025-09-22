package it.paoloadesso.gestioneordini.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreaProdottiDto {

    @NotBlank(message = "Il nome del prodotto è obbligatorio")
    private String nome;

    @NotBlank(message = "La categoria è obbligatoria")
    private String categoria;

    @NotNull(message = "Il prezzo è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
    private BigDecimal prezzo;

    public CreaProdottiDto(String nome, String categoria, BigDecimal prezzo) {
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
    }

    public CreaProdottiDto() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }
}
