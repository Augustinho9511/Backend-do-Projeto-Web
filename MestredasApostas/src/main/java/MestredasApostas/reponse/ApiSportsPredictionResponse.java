package MestredasApostas.reponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSportsPredictionResponse {

    @JsonProperty("response")
    private List<PredictionData> response;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static
    class PredictionData {
        private PredictionDetails predictions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    class PredictionDetails {
        // Adicionei os campos 'advice' e 'btts' aqui para corresponder ao JSON da API
        private String advice;
        private boolean btts;
    }
}

