package MestredasApostas.model.dto;

import MestredasApostas.model.enums.ApostaStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ApostaDTO {
    private String timeCasa;
    private String timeFora;
    private String liga;
    private LocalDateTime dataHorario;
    private String palpite;
    private BigDecimal valor;
    private BigDecimal odd;
    private ApostaStatusEnum status;
    private Double valorCashOutRecebido;
}
