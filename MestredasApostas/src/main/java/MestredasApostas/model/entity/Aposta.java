package MestredasApostas.model.entity;

import MestredasApostas.model.enums.ApostaStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "apostas")
public class Aposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private ApostaStatusEnum status;

    private String timeFora;

    private String timeCasa;

    private String liga;

    private LocalDateTime dataHorario;

    private Double valorCashOutRecebido;

    public Aposta() {
    }

    @Override
    public String toString() {

        return "Aposta{" +
                "id=" + id +
                ", jogoId=" + (jogo != null ? jogo.getJogoId() : null) +
                ", palpite='" + palpite + '\'' +
                ", valorAposta=" + valor +
                ", odd=" + odd +
                ", dataHorario='" + dataHorario + '\'' +
                ", timeCasa='" + timeCasa + '\'' +
                ", timeFora='" + timeFora + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}