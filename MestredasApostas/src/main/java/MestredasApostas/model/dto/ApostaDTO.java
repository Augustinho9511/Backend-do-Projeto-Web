package MestredasApostas.model.dto;

import MestredasApostas.model.enums.ApostaStatusEnum; // Importa o enum
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class ApostaDTO {
    // Getters e Setters
    private Long jogoApiId; // NOVO: Campo para o ID do jogo da API
    private String timeCasa;
    private String timeFora;
    private String liga;
    private LocalDateTime dataHorario;
    private String palpite;
    private BigDecimal valor;
    private BigDecimal odd;
    private ApostaStatusEnum status; // Tipo Enum para o status
    private BigDecimal valorCashOutRecebido;

    // Construtor padrão
    public ApostaDTO() {}

    // Construtor completo
    public ApostaDTO(Long jogoApiId, String timeCasa, String timeFora, String liga, LocalDateTime dataHorario, String palpite, BigDecimal valor, BigDecimal odd, ApostaStatusEnum status, BigDecimal valorCashOutRecebido) {
        this.jogoApiId = jogoApiId;
        this.timeCasa = timeCasa;
        this.timeFora = timeFora;
        this.liga = liga;
        this.dataHorario = dataHorario;
        this.palpite = palpite;
        this.valor = valor;
        this.odd = odd;
        this.status = status;
        this.valorCashOutRecebido = valorCashOutRecebido;
    }

}
