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


    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor;


    @Column(name = "odd", precision = 10, scale = 2)
    private BigDecimal odd;

    @Column(name = "status")
    private String status;

    private String timeFora;

    private String timeCasa;

    private String liga;

    private String DataHorario;

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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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


    public String getDataHorario() {
        return DataHorario;
    }

    public void setDataHorario(String dataHorario) {
        DataHorario = dataHorario;
    }

    public String getLiga() {
        return liga;
    }

    public void setLiga(String liga) {
        this.liga = liga;
    }

    public String getTimedecasa() {
        return timeCasa;
    }

    public void setTimecasa(String timeCasa) {
        this.timeCasa = timeCasa;
    }

    public String getTimedefora() {
        return timeFora;
    }

    public void setTimefora(String timeFora) {
        this.timeFora = timeFora;
    }
    @Override
    public String toString() {
        // Corrigido para evitar loop infinito com a relação Jogo
        return "Aposta{" +
                "apostaId=" + apostaId +
                ", jogoId=" + (jogo != null ? jogo.getJogoId() : null) +
                ", palpite='" + palpite + '\'' +
                ", valorAposta=" + valor +
                ", odd=" + odd +
                ", dataHorario='" + DataHorario + '\'' +
                ", timeCasa='" + timeCasa + '\'' +
                ", timeFora='" + timeFora + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}