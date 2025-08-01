package MestredasApostas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "apostas")
public class Aposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long apostaId;

    @ManyToOne
    @JoinColumn(name = "jogo_id")
    private Jogo jogo;

    @Column(name = "palpite")
    private String palpite;


    @Column(name = "valor_aposta", precision = 10, scale = 2)
    private BigDecimal valorAposta;


    @Column(name = "odd", precision = 10, scale = 2)
    private BigDecimal odd;

    @Column(name = "status")
    private String status;

    private String timedefora;

    private String timedecasa;

    private String liga;

    public Aposta() {
    }

    // Getters e Setters
    public Long getApostaId() {
        return apostaId;
    }

    public void setApostaId(Long apostaId) {
        this.apostaId = apostaId;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public String getPalpite() {
        return palpite;
    }

    public void setPalpite(String palpite) {
        this.palpite = palpite;
    }

    public BigDecimal getValorAposta() {
        return valorAposta;
    }

    public void setValorAposta(BigDecimal valorAposta) {
        this.valorAposta = valorAposta;
    }

    public BigDecimal getOdd() {
        return odd;
    }

    public void setOdd(BigDecimal odd) {
        this.odd = odd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        // Corrigido para evitar loop infinito com a relação Jogo
        return "Aposta{" +
                "apostaId=" + apostaId +
                ", jogoId=" + (jogo != null ? jogo.getJogoId() : null) +
                ", palpite='" + palpite + '\'' +
                ", valorAposta=" + valorAposta +
                ", odd=" + odd +
                ", status='" + status + '\'' +
                '}';
    }
}