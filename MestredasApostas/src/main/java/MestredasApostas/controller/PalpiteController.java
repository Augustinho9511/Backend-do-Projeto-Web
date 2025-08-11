package MestredasApostas.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ia")
public class PalpiteController {

    private final WebClient apiSportsWebClient;

    public PalpiteController(WebClient apiSportsWebClient) {
        this.apiSportsWebClient = apiSportsWebClient;
    }

    /**
     * Endpoint para buscar países e a contagem de jogos para a data especificada.
     * @param date A data para filtrar os jogos (formato yyyy-MM-dd).
     * @return Uma lista de objetos contendo o nome do país e a contagem de jogos.
     */
    @GetMapping("/countries")
    public List<CountryGamesCount> getCountries(@RequestParam String date) {
        System.out.println("DEBUG: getCountries - Buscando países com contagem de jogos para a data: " + date);

        Mono<ApiSportsFixtureResponse> responseMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            System.err.println("ERRO API-Sports (Países) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                            return new RuntimeException("Erro da API-Sports ao buscar países: " + body);
                        }))
                .bodyToMono(ApiSportsFixtureResponse.class);

        ApiSportsFixtureResponse apiResponse = null;
        try {
            apiResponse = responseMono.block();
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao receber resposta da API-Sports para países: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        if (apiResponse == null || apiResponse.getResponse() == null) {
            System.out.println("DEBUG: getCountries - Resposta da API-Sports foi nula ou vazia para a data: " + date);
            return Collections.emptyList();
        }

        System.out.println("DEBUG: getCountries - " + apiResponse.getResponse().size() + " jogos recebidos para a data: " + date);

        Map<String, Long> gamesByCountry = apiResponse.getResponse().stream()
                .filter(fixtureData -> fixtureData.getLeague() != null &&
                        fixtureData.getLeague().getCountry() != null)
                .collect(Collectors.groupingBy(
                        fixtureData -> fixtureData.getLeague().getCountry(),
                        Collectors.counting()
                ));

        return gamesByCountry.entrySet().stream()
                .map(entry -> new CountryGamesCount(entry.getKey(), entry.getValue()))
                .sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Endpoint para buscar ligas e a contagem de jogos para o país e data especificados.
     * @param country O nome do país para filtrar.
     * @param date A data para filtrar os jogos (formato yyyy-MM-dd).
     * @return Uma lista de objetos contendo o nome da liga e a contagem de jogos.
     */
    @GetMapping("/leagues")
    public List<LeagueGamesCount> getLeagues(@RequestParam String country, @RequestParam String date) {
        System.out.println("DEBUG: getLeagues - Buscando ligas com contagem de jogos para país: " + country + " e data: " + date);

        Mono<ApiSportsFixtureResponse> responseMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            System.err.println("ERRO API-Sports (Ligas) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                            return new RuntimeException("Erro da API-Sports ao buscar ligas: " + body);
                        }))
                .bodyToMono(ApiSportsFixtureResponse.class);

        ApiSportsFixtureResponse apiResponse = null;
        try {
            apiResponse = responseMono.block();
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao receber resposta da API-Sports para ligas: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        if (apiResponse == null || apiResponse.getResponse() == null) {
            System.out.println("DEBUG: getLeagues - Resposta da API-Sports foi nula ou vazia para país: " + country + " e data: " + date);
            return Collections.emptyList();
        }

        System.out.println("DEBUG: getLeagues - " + apiResponse.getResponse().size() + " jogos recebidos para país: " + country + " e data: " + date);

        Map<String, Long> gamesByLeague = apiResponse.getResponse().stream()
                .filter(fixtureData -> fixtureData.getLeague() != null &&
                        fixtureData.getLeague().getCountry() != null &&
                        fixtureData.getLeague().getCountry().equals(country) &&
                        fixtureData.getLeague().getName() != null)
                .collect(Collectors.groupingBy(
                        fixtureData -> fixtureData.getLeague().getName(),
                        Collectors.counting()
                ));

        System.out.println("DEBUG: getLeagues - Ligas encontradas: " + gamesByLeague.keySet().toString());

        return gamesByLeague.entrySet().stream()
                .map(entry -> new LeagueGamesCount(entry.getKey(), entry.getValue()))
                .sorted((l1, l2) -> l1.getName().compareTo(l2.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Endpoint para buscar jogos para a data, país e liga especificados.
     * @param date A data para filtrar os jogos (formato yyyy-MM-dd).
     * @param country O nome do país.
     * @param league O nome da liga.
     * @return Uma lista de objetos de jogos.
     */
    @GetMapping("/jogos")
    public List<JogoResponse> getJogos(
            @RequestParam String date,
            @RequestParam String country,
            @RequestParam String league) {

        System.out.println("DEBUG: getJogos - Buscando jogos para País: " + country + ", Liga: " + league + " e data: " + date);

        Mono<ApiSportsFixtureResponse> responseMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            System.err.println("ERRO API-Sports (Jogos) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                            return new RuntimeException("Erro da API-Sports ao buscar jogos: " + body);
                        }))
                .bodyToMono(ApiSportsFixtureResponse.class);

        ApiSportsFixtureResponse apiResponse = null;
        try {
            apiResponse = responseMono.block();
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao receber resposta da API-Sports para jogos: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        if (apiResponse == null || apiResponse.getResponse() == null) {
            System.out.println("DEBUG: getJogos - Resposta da API-Sports para jogos foi nula ou vazia.");
            return Collections.emptyList();
        }

        List<JogoResponse> jogosFiltrados = apiResponse.getResponse().stream()
                .filter(fixtureData -> fixtureData.getLeague() != null &&
                        fixtureData.getLeague().getCountry() != null &&
                        fixtureData.getLeague().getCountry().equals(country) &&
                        fixtureData.getLeague().getName() != null &&
                        fixtureData.getLeague().getName().equals(league))
                .map(this::mapToJogoResponse)
                .collect(Collectors.toList());

        System.out.println("DEBUG: getJogos - " + jogosFiltrados.size() + " jogos filtrados para a liga.");

        return jogosFiltrados;
    }

    /**
     * Endpoint para gerar palpites de gols, escanteios e resultado para um jogo usando a API-Sports.
     * @param jogoId O ID do jogo para o qual gerar os palpites.
     * @return Um objeto de resposta com os palpites estruturados.
     */
    @GetMapping("/palpites")
    public Mono<PredictionDTO> getPalpites(@RequestParam Long jogoId) {
        System.out.println("DEBUG: getPalpites - Buscando palpites reais para o ID do jogo: " + jogoId);

        return apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/predictions")
                        .queryParam("fixture", jogoId)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            System.err.println("ERRO API-Sports (Palpites) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                            return new RuntimeException("Erro da API-Sports ao buscar palpites: " + body);
                        }))
                .bodyToMono(ApiSportsPredictionResponse.class)
                .map(apiResponse -> {
                    if (apiResponse == null || apiResponse.getResponse() == null || apiResponse.getResponse().isEmpty()) {
                        System.err.println("ERRO: Nenhum palpite encontrado para o ID do jogo: " + jogoId);
                        return new PredictionDTO("N/A", "N/A", "Não foi possível encontrar o palpite.");
                    }

                    System.out.println("DEBUG: getPalpites - Resposta completa da API-Sports: " + apiResponse.getResponse().get(0));

                    Optional<PredictionData> predictionDataOpt = Optional.ofNullable(apiResponse.getResponse().get(0));
                    PredictionDetails predictionDetails = predictionDataOpt.map(PredictionData::getPredictions).orElse(null);

                    String gols = getPredictionSafely(predictionDetails != null ? predictionDetails.getGoals() : null, "goals");
                    String escanteios = getPredictionSafely(predictionDetails != null ? predictionDetails.getCorner_kicks() : null, "corner_kicks");
                    String resultado = getPredictionSafely(predictionDetails != null ? predictionDetails.getWin_or_draw() : null, "win_or_draw");

                    System.out.println("DEBUG: getPalpites - Palpites extraídos: Gols=" + gols + ", Escanteios=" + escanteios + ", Resultado=" + resultado);

                    return new PredictionDTO(gols, escanteios, resultado);
                })
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha ao buscar palpites na API-Sports: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(new PredictionDTO("Erro", "Erro", "Não foi possível buscar detalhes do palpite."));
                });
    }

    /**
     * Método auxiliar para extrair a string de previsão de um objeto que pode ser um Map, um Booleano, etc.
     * Este método foi corrigido para lidar com valores nulos dentro de mapas, como nos campos de gols.
     * @param predictionObject O objeto de previsão retornado pela API.
     * @param fieldName O nome do campo para logs.
     * @return A string de previsão ou "Não disponível" se a estrutura for inesperada.
     */
    private String getPredictionSafely(Object predictionObject, String fieldName) {
        if (predictionObject == null) {
            System.out.println("DEBUG: getPredictionSafely - Campo '" + fieldName + "' é nulo.");
            return "Não disponível";
        }
        System.out.println("DEBUG: getPredictionSafely - Verificando o campo '" + fieldName + "'. Tipo: " + predictionObject.getClass().getName() + ", Valor: " + predictionObject);

        if (predictionObject instanceof Map) {
            Map<String, Object> predictionMap = (Map<String, Object>) predictionObject;

            // Se o mapa contiver a chave "prediction" (formato esperado)
            if (predictionMap.containsKey("prediction")) {
                return Optional.ofNullable(predictionMap.get("prediction"))
                        .map(Object::toString)
                        .orElse("Não disponível");
            }
            // Se o mapa for para "goals" e tiver as chaves "home" e "away"
            else if ("goals".equals(fieldName) && predictionMap.containsKey("home") && predictionMap.containsKey("away")) {
                // CORREÇÃO: Usamos Objects.toString() para lidar com valores nulos
                String homeGoals = Objects.toString(predictionMap.get("home"), "Não disponível");
                String awayGoals = Objects.toString(predictionMap.get("away"), "Não disponível");
                return "Casa: " + homeGoals + ", Fora: " + awayGoals;
            }
        }
        // Se o objeto for um booleano (como para "win_or_draw")
        else if (predictionObject instanceof Boolean) {
            return (Boolean) predictionObject ? "Vitória ou Empate" : "Não disponível";
        }

        // Se o objeto for do tipo String
        else if (predictionObject instanceof String) {
            return (String) predictionObject;
        }

        // Caso de fallback para qualquer outro tipo ou estrutura inesperada
        System.out.println("DEBUG: getPredictionSafely - Tipo ou estrutura de dados não esperada para o campo '" + fieldName + "'. Tipo: " + predictionObject.getClass().getName() + ", Valor: " + predictionObject);
        return "Não disponível";
    }

    private JogoResponse mapToJogoResponse(FixtureData fixtureData) {
        JogoResponse jogoResponse = new JogoResponse();

        jogoResponse.setJogoId(fixtureData.getFixture() != null ? fixtureData.getFixture().getId() : null);
        jogoResponse.setHomeTeam(fixtureData.getTeams() != null && fixtureData.getTeams().getHome() != null ? fixtureData.getTeams().getHome().getName() : "N/A");
        jogoResponse.setAwayTeam(fixtureData.getTeams() != null && fixtureData.getTeams().getAway() != null ? fixtureData.getTeams().getAway().getName() : "N/A");
        jogoResponse.setLeagueName(fixtureData.getLeague() != null ? fixtureData.getLeague().getName() : "N/A");
        jogoResponse.setCountryName(fixtureData.getLeague() != null ? fixtureData.getLeague().getCountry() : "N/A");

        return jogoResponse;
    }

    // DTOs para o frontend
    @Getter @Setter @NoArgsConstructor
    public static class CountryGamesCount {
        private String name;
        private Long jogosCount;

        public CountryGamesCount(String name, Long jogosCount) {
            this.name = name;
            this.jogosCount = jogosCount;
        }
    }

    @Getter @Setter @NoArgsConstructor
    public static class LeagueGamesCount {
        private String name;
        private Long jogosCount;

        public LeagueGamesCount(String name, Long jogosCount) {
            this.name = name;
            this.jogosCount = jogosCount;
        }
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PredictionDTO {
        private String gols;
        private String escanteios;
        private String resultado;

        public PredictionDTO(String gols, String escanteios, String resultado) {
            this.gols = gols;
            this.escanteios = escanteios;
            this.resultado = resultado;
        }
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JogoResponse {
        private Long jogoId;
        private String homeTeam;
        private String awayTeam;
        private String leagueName;
        private String countryName;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsFixtureResponse {
        @JsonProperty("response")
        private List<FixtureData> response;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsLeagueResponse {
        @JsonProperty("response")
        private List<LeagueData> response;
    }

    // DTO para a resposta da API de Previsões
    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsPredictionResponse {
        @JsonProperty("response")
        private List<PredictionData> response;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PredictionData {
        private PredictionDetails predictions;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PredictionDetails {
        private Object goals;
        @JsonProperty("corner_kicks")
        private Object corner_kicks;
        @JsonProperty("win_or_draw")
        private Object win_or_draw;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prediction {
        private String prediction;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureData {
        private FixtureDetails fixture;
        private LeagueDetails league;
        private TeamsDetails teams;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureDetails {
        private Long id;
        private String date;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueDetails {
        private Long id;
        private String name;
        private String country;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueData {
        private LeagueDetails league;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsDetails {
        private TeamDetails home;
        private TeamDetails away;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDetails {
        private String name;
    }
}
