package MestredasApostas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Modelo de dados para jogo.
 * Representa a tabela 'jogos' no banco de dados.
 */
@Entity
@Table(name = "jogos")
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jogoId;

    @Column(name = "equipa_casa")
    private String equipaCasa;

    @Column(name = "equipa_fora")
    private String equipaFora;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "league_id")
    private Integer leagueId;

    @Column(name = "resultado")
    private String resultado;

    public Jogo() {
    }

    // Getters e Setters
    public Long getJogoId() {
        return jogoId;
    }

    public void setJogoId(Long jogoId) {
        this.jogoId = jogoId;
    }

    public String getEquipaCasa() {
        return equipaCasa;
    }

    public void setEquipaCasa(String equipaCasa) {
        this.equipaCasa = equipaCasa;
    }

    public String getEquipaFora() {
        return equipaFora;
    }

    public void setEquipaFora(String equipaFora) {
        this.equipaFora = equipaFora;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}