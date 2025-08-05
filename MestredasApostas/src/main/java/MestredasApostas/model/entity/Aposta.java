package MestredasApostas.model.entity;

import MestredasApostas.model.enums.ApostaStatusEnum;
import jakarta.persistence.*;
import lombok.Data; // Certifique-se de que Lombok está configurado no seu projeto

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data // Anotação do Lombok para gerar getters, setters, toString, equals e hashCode
@Entity // Indica que esta classe é uma entidade JPA e será mapeada para uma tabela no banco de dados
@Table(name = "apostas") // Define o nome da tabela no banco de dados
public class Aposta {

    @Id // Marca o campo 'id' como a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura a geração automática de valores para a chave primária
    private Long id;

    // REMOVIDO: @ManyToOne private Jogo jogo;
    // As informações do jogo são armazenadas diretamente na aposta:
    @Column(name = "jogo_api_id") // ID do jogo na API externa
    private Long jogoApiId;

    @Column(name = "time_casa")
    private String timeCasa;

    @Column(name = "time_fora")
    private String timeFora;

    @Column(name = "liga")
    private String liga;

    @Column(name = "data_horario")
    private LocalDateTime dataHorario;

    @Column(name = "palpite")
    private String palpite;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "odd")
    private BigDecimal odd;

    @Enumerated(EnumType.STRING) // Mapeia o enum para String no banco de dados
    @Column(name = "status")
    private ApostaStatusEnum status; // Usando o Enum diretamente

    @Column(name = "valor_cashout_recebido")
    private BigDecimal valorCashOutRecebido;

    // Construtor padrão (necessário para JPA)
    public Aposta() {
    }

    // Construtor com todos os campos para facilitar a criação de novas apostas
    public Aposta(Long jogoApiId, String timeCasa, String timeFora, String liga, LocalDateTime dataHorario, String palpite, BigDecimal valor, BigDecimal odd, ApostaStatusEnum status, BigDecimal valorCashOutRecebido) {
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


    @Override
    public String toString() {
        return "Aposta{" +
                "id=" + id +
                ", jogoApiId=" + jogoApiId + // Alterado para refletir o campo direto
                ", timeCasa='" + timeCasa + '\'' +
                ", timeFora='" + timeFora + '\'' +
                ", liga='" + liga + '\'' +
                ", dataHorario=" + dataHorario +
                ", palpite='" + palpite + '\'' +
                ", valor=" + valor +
                ", odd=" + odd +
                ", status=" + status +
                ", valorCashOutRecebido=" + valorCashOutRecebido +
                '}';
    }
}
