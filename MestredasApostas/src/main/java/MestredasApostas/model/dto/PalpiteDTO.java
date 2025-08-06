package MestredasApostas.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PalpiteDTO {
    private Long jogoId;
    private String timeCasa;
    private String timeFora;
    private String liga;
    private LocalDateTime dataHorario;
    private String palpite;
    private BigDecimal odd;
    private String analiseGols;

    public PalpiteDTO() {
    }

    // Construtor atualizado
    public PalpiteDTO(Long jogoId, String timeCasa, String timeFora, String liga, LocalDateTime dataHorario, String palpite, BigDecimal odd, String analiseGols) {
        this.jogoId = jogoId;
        this.timeCasa = timeCasa;
        this.timeFora = timeFora;
        this.liga = liga;
        this.dataHorario = dataHorario;
        this.palpite = palpite;
        this.odd = odd;
        this.analiseGols = analiseGols;
    }

    // Getters e Setters
    public Long getJogoId() {
        return jogoId;
    }

    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public String getTimeCasa() {
        return timeCasa;
    }

    public void setTimeCasa(String timeCasa) {
        this.timeCasa = timeCasa;
    }

    public String getTimeFora() {
        return timeFora;
    }

    public void setTimeFora(String timeFora) {
        this.timeFora = timeFora;
    }

    public String getLiga() {
        return liga;
    }

    public void setLiga(String liga) {
        this.liga = liga;
    }

    public LocalDateTime getDataHorario() {
        return dataHorario;
    }

    public void setDataHorario(LocalDateTime dataHorario) {
        this.dataHorario = dataHorario;
    }

    public String getPalpite() {
        return palpite;
    }

    public void setPalpite(String palpite) {
        this.palpite = palpite;
    }

    public BigDecimal getOdd() {
        return odd;
    }

    public void setOdd(BigDecimal odd) {
        this.odd = odd;
    }

    public String getAnaliseGols() {
        return analiseGols;
    }

    public void setAnaliseGols(String analiseGols) {
        this.analiseGols = analiseGols;
    }

    @Override
    public String toString() {
        return "PalpiteDTO{" +
                "jogoId=" + jogoId +
                ", timeCasa='" + timeCasa + '\'' +
                ", timeFora='" + timeFora + '\'' +
                ", liga='" + liga + '\'' +
                ", dataHorario=" + dataHorario +
                ", palpite='" + palpite + '\'' +
                ", odd=" + odd +
                ", analiseGols='" + analiseGols + '\'' + // Inclui analiseGols no toString
                '}';
    }
}
