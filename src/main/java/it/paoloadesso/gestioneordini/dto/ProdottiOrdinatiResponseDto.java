package it.paoloadesso.gestioneordini.dto;

import it.paoloadesso.gestioneordini.enums.StatoOrdine;
import it.paoloadesso.gestioneordini.enums.StatoPagato;

import java.time.LocalDate;

public class ProdottiOrdinatiResponseDto {
    public static class ProdottoOrdinatoDto {
        private Long idProdotto;
        private Long quantitaProdotto;
        private StatoPagato statoPagato;

        public Long getIdProdotto() {
            return idProdotto;
        }

        public void setIdProdotto(Long idProdotto) {
            this.idProdotto = idProdotto;
        }

        public Long getQuantitaProdotto() {
            return quantitaProdotto;
        }

        public void setQuantitaProdotto(Long quantitaProdotto) {
            this.quantitaProdotto = quantitaProdotto;
        }

        public StatoPagato getStatoPagato() {
            return statoPagato;
        }

        public void setStatoPagato(StatoPagato statoPagato) {
            this.statoPagato = statoPagato;
        }
    }


}
