package MestredasApostas.model.dto;

import MestredasApostas.model.enums.ApostaStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ApostaDTO {
    private String timeCasa;
    private String timeFora;
    private String liga;
    private LocalDateTime data;
    private String palpite;
    private BigDecimal valor;
    private BigDecimal odd;
    private ApostaStatusEnum status;
    private Double valorCashOutRecebido;
}
