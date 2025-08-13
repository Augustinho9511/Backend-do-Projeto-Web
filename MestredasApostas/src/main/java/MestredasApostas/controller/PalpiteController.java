package MestredasApostas.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ia")
public class PalpiteController {

    private final WebClient apiSportsWebClient;

    public PalpiteController(WebClient apiSportsWebClient) {
        this.apiSportsWebClient = apiSportsWebClient;
    }

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

    @GetMapping("/analise-completa")
    public Mono<AnaliseCompletaDTO> getAnaliseCompleta(@RequestParam Long jogoId) {
        System.out.println("DEBUG: getAnaliseCompleta - Buscando análise completa para o ID do jogo: " + jogoId);

        // 1. Chama o endpoint de palpites
        Mono<ApiSportsPredictionResponse> predictionsMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/predictions").queryParam("fixture", jogoId).build())
                .retrieve()
                .bodyToMono(ApiSportsPredictionResponse.class)
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha ao buscar palpites na API-Sports: " + e.getMessage());
                    return Mono.empty();
                });

        // 2. Chama o endpoint de eventos (para cartões e escanteios)
        Mono<ApiSportsEventsResponse> eventsMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fixtures/events").queryParam("fixture", jogoId).build())
                .retrieve()
                .bodyToMono(ApiSportsEventsResponse.class)
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha ao buscar eventos na API-Sports: " + e.getMessage());
                    return Mono.empty();
                });

        // Combina os resultados das duas chamadas
        return Mono.zip(predictionsMono, eventsMono)
                .map(tuple -> {
                    ApiSportsPredictionResponse predictionResponse = tuple.getT1();
                    ApiSportsEventsResponse eventsResponse = tuple.getT2();

                    // Processa a resposta de palpites
                    String resultado = "Não disponível";
                    String gols = "Não disponível";
                    String btts = "Não disponível";
                    if (predictionResponse != null && predictionResponse.getResponse() != null && !predictionResponse.getResponse().isEmpty()) {
                        PredictionData predictionData = predictionResponse.getResponse().get(0);
                        PredictionDetails predictionDetails = Optional.ofNullable(predictionData.getPredictions()).orElse(null);
                        if (predictionDetails != null) {
                            String advice = Optional.ofNullable(predictionDetails.getAdvice()).orElse("Não disponível");
                            Map<String, String> translatedData = splitAndTranslateAdvice(advice);
                            resultado = translatedData.get("resultado");
                            gols = translatedData.get("gols");
                            btts = Optional.ofNullable(predictionDetails.isBtts()).map(bttsValue -> bttsValue ? "Sim" : "Não").orElse("Não disponível");
                        }
                    }

                    // Processa a resposta de eventos (cartões e escanteios)
                    long totalCards = 0;
                    long totalCorners = 0;
                    if (eventsResponse != null && eventsResponse.getResponse() != null) {
                        totalCards = eventsResponse.getResponse().stream()
                                .filter(event -> "Card".equalsIgnoreCase(event.getType()))
                                .count();
                        totalCorners = eventsResponse.getResponse().stream()
                                .filter(event -> "Corner Kick".equalsIgnoreCase(event.getType()))
                                .count();
                    }

                    return new AnaliseCompletaDTO(resultado, gols, btts, String.valueOf(totalCards), String.valueOf(totalCorners));
                })
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha geral na análise completa: " + e.getMessage());
                    return Mono.just(new AnaliseCompletaDTO("Erro", "Erro", "Erro", "Erro", "Erro"));
                });
    }

    private Map<String, String> splitAndTranslateAdvice(String advice) {
        Map<String, String> translatedData = new HashMap<>();
        String resultado = "Não disponível";
        String gols = "Não disponível";

        if (advice != null && !advice.isEmpty()) {
            // Tenta encontrar a parte dos gols primeiro
            String goalsPattern = "([-+]?\\d+\\.\\d+|[-+]?\\d+) goals";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(goalsPattern);
            java.util.regex.Matcher matcher = pattern.matcher(advice);
            if (matcher.find()) {
                gols = matcher.group(0).replace("goals", "Gols");
                // Remove a parte dos gols para isolar o resultado
                advice = advice.replace(matcher.group(0), "").trim();
                // Remove o "and" se ele estiver no final
                if (advice.endsWith(" and")) {
                    advice = advice.substring(0, advice.length() - 4).trim();
                }
            }

            // Tenta encontrar o resultado com o texto restante
            if (!advice.isEmpty()) {
                String tempResultado = advice;
                if (tempResultado.contains("Combo Double chance")) {
                    tempResultado = tempResultado.replace("Combo Double chance", "Combo Chance Dupla");
                } else if (tempResultado.contains("Double chance")) {
                    tempResultado = tempResultado.replace("Double chance", "Chance Dupla");
                }
                resultado = tempResultado.replace(" or draw", " ou empate").trim();
            }
        }

        translatedData.put("resultado", resultado);
        translatedData.put("gols", gols);
        return translatedData;
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
    public static class ApiSportsPredictionResponse {
        @JsonProperty("response")
        private List<PredictionData> response;
    }

    @Getter @Setter @NoArgsConstructor @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PredictionData {
        private PredictionDetails predictions;
    }

    @Getter @Setter @NoArgsConstructor @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PredictionDetails {
        private WinnerDetails winner;
        @JsonProperty("win_or_draw")
        private boolean winOrDraw;
        @JsonProperty("under_over")
        private Object underOver;
        private GoalsDetails goals;
        private String advice;
        private Map<String, String> percent;
        @JsonProperty("btts")
        private boolean btts;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WinnerDetails {
        private Long id;
        private String name;
        private String comment;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoalsDetails {
        private String home;
        private String away;
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
    public static class TeamsDetails {
        private TeamDetails home;
        private TeamDetails away;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDetails {
        private String name;
    }

    // DTOs para o endpoint /fixtures/events
    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsEventsResponse {
        @JsonProperty("response")
        private List<EventData> response;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventData {
        private String type;
    }

    // Novo DTO para a resposta combinada
    @Getter @Setter @NoArgsConstructor
    public static class AnaliseCompletaDTO {
        private String resultado;
        private String gols;
        private String btts;
        private String cartoes;
        private String escanteios;

        public AnaliseCompletaDTO(String resultado, String gols, String btts, String cartoes, String escanteios) {
            this.resultado = resultado;
            this.gols = gols;
            this.btts = btts;
            this.cartoes = cartoes;
            this.escanteios = escanteios;
        }
    }
}