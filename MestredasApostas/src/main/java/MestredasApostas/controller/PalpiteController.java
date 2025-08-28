package MestredasApostas.controller;

import MestredasApostas.reponse.ApiSportsStatisticsResponse;
import MestredasApostas.reponse.ApiSportsFixtureResponse;
import MestredasApostas.reponse.ApiSportsPredictionResponse;
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
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Helper method to safely parse an Object to a Double.
     * It handles Integers, Strings, and null values.
     *
     * @param value The object to parse.
     * @return The parsed double value, or 0.0 if parsing fails.
     */
    private Double safeParseDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                // Remove non-numeric characters and then parse
                String numericString = ((String) value).replaceAll("[^\\d.]", "");
                return Double.parseDouble(numericString);
            } catch (NumberFormatException e) {
                // If it's a string but not a number, return 0.0
                return 0.0;
            }
        }
        return 0.0;
    }

    private Mono<Map<String, Double>> getPalpiteChutesEFaltas(Long teamId) {
        return apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("team", teamId)
                        .queryParam("last", 5)
                        .build())
                .retrieve()
                .bodyToMono(ApiSportsFixtureResponse.class)
                .flatMap(fixtureResponse -> {
                    if (fixtureResponse == null || fixtureResponse.getResponse() == null || fixtureResponse.getResponse().isEmpty()) {
                        return Mono.just(Collections.<String, Double>emptyMap());
                    }

                    List<Mono<ApiSportsStatisticsResponse>> statsMonos = fixtureResponse.getResponse().stream()
                            .filter(fixtureData -> fixtureData.getFixture() != null && "Match Finished".equals(fixtureData.getFixture().getStatus().getShortStatus()))
                            .map(fixtureData -> fixtureData.getFixture().getId())
                            .map(id -> apiSportsWebClient.get()
                                    .uri(uriBuilder -> uriBuilder
                                            .path("/fixtures/statistics")
                                            .queryParam("fixture", id)
                                            .build())
                                    .retrieve()
                                    .bodyToMono(ApiSportsStatisticsResponse.class)
                                    .onErrorResume(e -> Mono.empty()))
                            .collect(Collectors.toList());

                    return Mono.zip(statsMonos, results -> {
                        double totalShots = 0;
                        double totalFouls = 0;
                        int gamesCount = 0;

                        for (Object result : results) {
                            if (result instanceof ApiSportsStatisticsResponse) {
                                ApiSportsStatisticsResponse statsResponse = (ApiSportsStatisticsResponse) result;
                                if (statsResponse.getResponse() != null) {
                                    for (ApiSportsStatisticsResponse.StatisticsDataWrapper dataWrapper : statsResponse.getResponse()) {
                                        if (dataWrapper.getTeam().getId().equals(teamId)) {
                                            gamesCount++;
                                            for (ApiSportsStatisticsResponse.Statistic stat : dataWrapper.getStatistics()) {
                                                String statType = stat.getType();
                                                if ("Shots on Goal".equals(statType) || "Total Shots".equals(statType)) {
                                                    totalShots += safeParseDouble(stat.getValue());
                                                }
                                                if ("Fouls".equals(statType)) {
                                                    totalFouls += safeParseDouble(stat.getValue());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Map<String, Double> finalStats = new HashMap<>();
                        finalStats.put("media_chutes", gamesCount > 0 ? totalShots / gamesCount : 0.0);
                        finalStats.put("media_faltas", gamesCount > 0 ? totalFouls / gamesCount : 0.0);
                        return finalStats;
                    });
                })
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha geral no cálculo de chutes e faltas: " + e.getMessage());
                    return Mono.just(Collections.<String, Double>emptyMap());
                });
    }

    private Mono<Map<String, Double>> getPalpiteEscanteios(Long teamId) {
        return apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("team", teamId)
                        .queryParam("last", 5)
                        .build())
                .retrieve()
                .bodyToMono(ApiSportsFixtureResponse.class)
                .flatMap(fixtureResponse -> {
                    if (fixtureResponse == null || fixtureResponse.getResponse() == null || fixtureResponse.getResponse().isEmpty()) {
                        return Mono.just(Collections.<String, Double>emptyMap());
                    }

                    List<Mono<ApiSportsStatisticsResponse>> statsMonos = fixtureResponse.getResponse().stream()
                            .filter(fixtureData -> fixtureData.getFixture() != null && "Match Finished".equals(fixtureData.getFixture().getStatus().getShortStatus()))
                            .map(fixtureData -> fixtureData.getFixture().getId())
                            .map(id -> apiSportsWebClient.get()
                                    .uri(uriBuilder -> uriBuilder
                                            .path("/fixtures/statistics")
                                            .queryParam("fixture", id)
                                            .build())
                                    .retrieve()
                                    .bodyToMono(ApiSportsStatisticsResponse.class)
                                    .onErrorResume(e -> Mono.empty()))
                            .collect(Collectors.toList());

                    return Mono.zip(statsMonos, results -> {
                        double totalCorners = 0;
                        int gamesCount = 0;

                        for (Object result : results) {
                            if (result instanceof ApiSportsStatisticsResponse) {
                                ApiSportsStatisticsResponse statsResponse = (ApiSportsStatisticsResponse) result;
                                if (statsResponse.getResponse() != null) {
                                    for (ApiSportsStatisticsResponse.StatisticsDataWrapper dataWrapper : statsResponse.getResponse()) {
                                        if (dataWrapper.getTeam().getId().equals(teamId)) {
                                            gamesCount++;
                                            for (ApiSportsStatisticsResponse.Statistic stat : dataWrapper.getStatistics()) {
                                                if ("Corner Kicks".equals(stat.getType())) {
                                                    totalCorners += safeParseDouble(stat.getValue());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Map<String, Double> finalStats = new HashMap<>();
                        finalStats.put("media_escanteios", gamesCount > 0 ? totalCorners / gamesCount : 0.0);
                        return finalStats;
                    });
                })
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha geral no cálculo de escanteios: " + e.getMessage());
                    return Mono.just(Collections.<String, Double>emptyMap());
                });
    }

    @GetMapping("/analise-completa")
    public Mono<AnaliseCompletaDTO> getAnaliseCompleta(@RequestParam Long jogoId) {
        System.out.println("DEBUG: getAnaliseCompleta - Buscando análise completa para o ID do jogo: " + jogoId);

        Mono<ApiSportsFixtureResponse> fixtureDetailsMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fixtures").queryParam("id", jogoId).build())
                .retrieve()
                .bodyToMono(ApiSportsFixtureResponse.class);

        Mono<ApiSportsPredictionResponse> predictionsMono = apiSportsWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/predictions").queryParam("fixture", jogoId).build())
                .retrieve()
                .bodyToMono(ApiSportsPredictionResponse.class)
                .onErrorResume(e -> Mono.just(new ApiSportsPredictionResponse()));

        return Mono.zip(fixtureDetailsMono, predictionsMono)
                .flatMap(tuple -> {
                    ApiSportsFixtureResponse fixtureResponse = tuple.getT1();
                    ApiSportsPredictionResponse predictionResponse = tuple.getT2();

                    if (fixtureResponse == null || fixtureResponse.getResponse() == null || fixtureResponse.getResponse().isEmpty()) {
                        return Mono.just(new AnaliseCompletaDTO("Erro", "Erro", "Erro", "Erro", "Erro", "Erro"));
                    }

                    FixtureData fixtureData = fixtureResponse.getResponse().get(0);
                    Long homeTeamId = fixtureData.getTeams().getHome().getId();
                    Long awayTeamId = fixtureData.getTeams().getAway().getId();

                    Mono<Map<String, Double>> homeChutesEFaltasMono = getPalpiteChutesEFaltas(homeTeamId);
                    Mono<Map<String, Double>> awayChutesEFaltasMono = getPalpiteChutesEFaltas(awayTeamId);

                    Mono<Map<String, Double>> homeEscanteiosMono = getPalpiteEscanteios(homeTeamId);
                    Mono<Map<String, Double>> awayEscanteiosMono = getPalpiteEscanteios(awayTeamId);

                    return Mono.zip(homeChutesEFaltasMono, awayChutesEFaltasMono, homeEscanteiosMono, awayEscanteiosMono)
                            .map(statsTuple -> {
                                Map<String, Double> homeChutesEFaltas = statsTuple.getT1();
                                Map<String, Double> awayChutesEFaltas = statsTuple.getT2();
                                Map<String, Double> homeEscanteios = statsTuple.getT3();
                                Map<String, Double> awayEscanteios = statsTuple.getT4();

                                String resultado = "Não disponível";
                                String gols = "Não disponível";
                                String btts = "Não disponível";

                                if (predictionResponse != null && predictionResponse.getResponse() != null && !predictionResponse.getResponse().isEmpty()) {
                                    PredictionData predictionData = predictionResponse.getResponse().get(0);
                                    PredictionDetails predictionDetails = Optional.ofNullable(predictionData.getPredictions()).orElse(null);
                                    if (predictionDetails != null) {
                                        // Extrair 'resultado' e 'gols' de forma mais robusta
                                        String advice = Optional.ofNullable(predictionDetails.getAdvice()).orElse("Não disponível");
                                        Map<String, String> translatedData = splitAndTranslateAdvice(advice);
                                        resultado = translatedData.get("resultado");

                                        // Preferir a informação direta de GoalsDetails se disponível
                                        GoalsDetails goalsDetails = predictionDetails.getGoals();
                                        if (goalsDetails != null && goalsDetails.getHome() != null && goalsDetails.getAway() != null) {
                                            gols = "Casa: " + goalsDetails.getHome() + ", Fora: " + goalsDetails.getAway();
                                        } else {
                                            gols = translatedData.get("gols");
                                        }

                                        btts = Optional.ofNullable(predictionDetails.isBtts()).map(bttsValue -> bttsValue ? "Sim" : "Não").orElse("Não disponível");
                                    }
                                }

                                String chutes = String.format("%.2f", homeChutesEFaltas.getOrDefault("media_chutes", 0.0)) + " vs " + String.format("%.2f", awayChutesEFaltas.getOrDefault("media_chutes", 0.0));
                                String faltas = String.format("%.2f", homeChutesEFaltas.getOrDefault("media_faltas", 0.0)) + " vs " + String.format("%.2f", awayChutesEFaltas.getOrDefault("media_faltas", 0.0));
                                String escanteios = String.format("%.2f", homeEscanteios.getOrDefault("media_escanteios", 0.0)) + " vs " + String.format("%.2f", awayEscanteios.getOrDefault("media_escanteios", 0.0));

                                return new AnaliseCompletaDTO(resultado, gols, btts, chutes, faltas, escanteios);
                            });
                })
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha geral na análise completa: " + e.getMessage());
                    return Mono.just(new AnaliseCompletaDTO("Erro", "Erro", "Erro", "Erro", "Erro", "Erro"));
                });
    }

    private Map<String, String> splitAndTranslateAdvice(String advice) {
        Map<String, String> translatedData = new HashMap<>();
        String resultado = "Não disponível";
        String gols = "Não disponível";

        String cleanAdvice = advice;

        Pattern goalsPattern = Pattern.compile("([-+]?\\d+\\.\\d+|[-+]?\\d+) goals");
        Matcher goalsMatcher = goalsPattern.matcher(cleanAdvice);
        if (goalsMatcher.find()) {
            gols = goalsMatcher.group(0).replace("goals", "Gols");
            cleanAdvice = cleanAdvice.replace(goalsMatcher.group(0), "").trim();
        }

        Pattern goalsTeamsPattern = Pattern.compile("Goals: Casa: ([-+]?\\d+\\.\\d+), Fora: ([-+]?\\d+\\.\\d+)");
        Matcher goalsTeamsMatcher = goalsTeamsPattern.matcher(advice);
        if (goalsTeamsMatcher.find()) {
            gols = "Casa: " + goalsTeamsMatcher.group(1) + ", Fora: " + goalsTeamsMatcher.group(2);
            cleanAdvice = cleanAdvice.replace(goalsTeamsMatcher.group(0), "").trim();
        }

        if (cleanAdvice.endsWith(" and")) {
            cleanAdvice = cleanAdvice.substring(0, cleanAdvice.length() - 4).trim();
        }

        if (!cleanAdvice.isEmpty() && !"Não disponível".equals(cleanAdvice)) {
            String tempResultado = cleanAdvice;

            tempResultado = tempResultado.replace("Combo Double chance", "Combo Chance Dupla");
            tempResultado = tempResultado.replace("Double chance", "Chance Dupla");
            tempResultado = tempResultado.replace(" or draw", " ou empate");
            tempResultado = tempResultado.replace(" to win", " vence");

            Pattern comboWinnerPattern = Pattern.compile("Combo Winner : (.+)");
            Matcher comboWinnerMatcher = comboWinnerPattern.matcher(tempResultado);
            if (comboWinnerMatcher.find()) {
                tempResultado = "Combo Vencedor: " + comboWinnerMatcher.group(1).trim();
            }

            Pattern winnerPattern = Pattern.compile("Winner : (.+)");
            Matcher winnerMatcher = winnerPattern.matcher(tempResultado);
            if (winnerMatcher.find()) {
                tempResultado = "Vencedor: " + winnerMatcher.group(1).trim();
            }

            resultado = tempResultado.trim();
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
    public static class FixtureData {
        private FixtureDetails fixture;
        private LeagueDetails league;
        private TeamsDetails teams;
        private ScoreDetails score;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureDetails {
        private Long id;
        private String date;
        private StatusDetails status;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusDetails {
        @JsonProperty("short")
        private String shortStatus;
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
        private TeamIdDetails home;
        private TeamIdDetails away;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamIdDetails {
        private Long id;
        private String name;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDetails {
        private String name;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoreDetails {
        private ScoreTime fulltime;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoreTime {
        private Integer home;
        private Integer away;
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

    @Getter @Setter @NoArgsConstructor @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WinnerDetails {
        private Long id;
        private String name;
        private String comment;
    }

    @Getter @Setter @NoArgsConstructor @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoalsDetails {
        private String home;
        private String away;
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsStatisticsResponse {
        @JsonProperty("response")
        private List<StatisticsDataWrapper> response;

        @Getter @Setter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
        public static class StatisticsDataWrapper {
            private TeamData team;
            private List<Statistic> statistics;
        }

        @Getter @Setter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TeamData {
            private Long id;
            private String name;
        }

        @Getter @Setter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Statistic {
            private String type;
            private Object value;
        }
    }

    @Getter @Setter @NoArgsConstructor
    public static class AnaliseCompletaDTO {
        private String resultado;
        private String gols;
        private String btts;
        private String chutes;
        private String faltas;
        private String escanteios;

        public AnaliseCompletaDTO(String resultado, String gols, String btts, String chutes, String faltas, String escanteios) {
            this.resultado = resultado;
            this.gols = gols;
            this.btts = btts;
            this.chutes = chutes;
            this.faltas = faltas;
            this.escanteios = escanteios;
        }
    }
}
