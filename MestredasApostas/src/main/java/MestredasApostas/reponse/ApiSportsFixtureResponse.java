package MestredasApostas.reponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO para mapear a resposta da API-Sports para a requisição de fixtures (jogos).
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSportsFixtureResponse {

    @JsonProperty("response")
    private List<FixtureData> response;

    /**
     * Detalhes do jogo.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Fixture {
        private Long id;
        private String date;
        private Status status;
    }

    /**
     * Classe interna para os dados de cada jogo.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class FixtureData {
        private Fixture fixture;
        private League league;
        private Teams teams;
        private Goals goals;
    }

    /**
     * Gols do jogo.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Goals {
        private Integer home;
        private Integer away;
    }

    /**
     * Detalhes da liga.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class League {
        private Long id;
        private String name;
        private String country;
        private String round;
    }

    /**
     * Times do jogo.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Teams {
        private Team home;
        private Team away;
    }

    /**
     * Status do jogo.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Status {
        private String shortStatus;
        @JsonProperty("elapsed")
        private Integer timeElapsed;
    }

    /**
     * Detalhes de um time.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class Team {
        private Long id;
        private String name;
    }
}


