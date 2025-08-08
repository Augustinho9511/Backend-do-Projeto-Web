package MestredasApostas.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ia")
public class PalpiteController {

    private final WebClient webClient;

    @Autowired
    public PalpiteController(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Endpoint para buscar países e a contagem de jogos para a data especificada.
     * @param date A data para filtrar os jogos (formato yyyy-MM-dd).
     * @return Uma lista de objetos contendo o nome do país e a contagem de jogos.
     */
    @GetMapping("/countries")
    public List<CountryGamesCount> getCountries(@RequestParam String date) {
        System.out.println("DEBUG: getCountries - Buscando países com contagem de jogos para a data: " + date);

        Mono<ApiSportsFixtureResponse> responseMono = webClient.get()
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

        Mono<ApiSportsFixtureResponse> responseMono = webClient.get()
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

        Mono<ApiSportsFixtureResponse> responseMono = webClient.get()
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
     * Endpoint para gerar um palpite de jogo usando o Gemini API.
     * @param fixtureId O ID do jogo para o qual gerar o palpite.
     * @return Um objeto de resposta de palpite.
     */
    @GetMapping("/predict")
    public Mono<PredictionResponse> getPrediction(@RequestParam Long fixtureId) {
        System.out.println("DEBUG: getPrediction - Buscando detalhes do jogo para o ID: " + fixtureId);

        // Primeiro, obtenha os detalhes do jogo
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("id", fixtureId)
                        .build())
                .retrieve()
                .bodyToMono(ApiSportsFixtureResponse.class)
                .flatMap(apiResponse -> {
                    if (apiResponse == null || apiResponse.getResponse() == null || apiResponse.getResponse().isEmpty()) {
                        return Mono.just(new PredictionResponse("Não foi possível encontrar detalhes do jogo."));
                    }

                    FixtureData fixtureData = apiResponse.getResponse().get(0);
                    String homeTeam = fixtureData.getTeams().getHome().getName();
                    String awayTeam = fixtureData.getTeams().getAway().getName();
                    String leagueName = fixtureData.getLeague().getName();

                    String prompt = String.format("Faça uma análise de jogo e forneça um palpite detalhado para a partida entre %s e %s, que faz parte da %s. Considere fatores como a forma recente dos times e o histórico de confrontos. Forneça o palpite de forma clara e profissional.", homeTeam, awayTeam, leagueName);

                    System.out.println("DEBUG: getPrediction - Prompt para o Gemini: " + prompt);

                    // Chame a API do Gemini com o prompt
                    return webClient.post()
                            .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=")
                            .bodyValue(Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))))
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(geminiResponse -> {
                                try {
                                    String predictionText = ((Map<String, Object>) ((List) geminiResponse.get("candidates")).get(0)).get("content").toString();
                                    return new PredictionResponse(predictionText);
                                } catch (Exception e) {
                                    System.err.println("ERRO: Falha ao analisar resposta do Gemini: " + e.getMessage());
                                    return new PredictionResponse("Ocorreu um erro ao gerar o palpite. Tente novamente mais tarde.");
                                }
                            })
                            .onErrorResume(e -> {
                                System.err.println("ERRO: Falha ao chamar a API do Gemini: " + e.getMessage());
                                return Mono.just(new PredictionResponse("Ocorreu um erro ao gerar o palpite. Tente novamente mais tarde."));
                            });
                })
                .onErrorResume(e -> {
                    System.err.println("ERRO: Falha ao buscar detalhes do jogo na API-Sports: " + e.getMessage());
                    return Mono.just(new PredictionResponse("Não foi possível buscar detalhes do jogo para gerar o palpite."));
                });
    }

    private String getLeagueId(String country, String league) {
        Mono<ApiSportsLeagueResponse> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/leagues")
                        .queryParam("country", country)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            System.err.println("ERRO API-Sports (ID Liga) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                            return new RuntimeException("Erro da API-Sports ao buscar ID da liga: " + body);
                        }))
                .bodyToMono(ApiSportsLeagueResponse.class);

        ApiSportsLeagueResponse apiResponse = null;
        try {
            apiResponse = responseMono.block();
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao receber resposta da API-Sports para ID da Liga: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        if (apiResponse == null || apiResponse.getResponse() == null) {
            System.out.println("DEBUG: getLeagueId - Resposta da API-Sports para ligas foi nula ou vazia para país: " + country);
            return null;
        }

        return apiResponse.getResponse().stream()
                .filter(leagueData -> leagueData != null && leagueData.getLeague() != null && leagueData.getLeague().getName() != null && leagueData.getLeague().getName().equals(league))
                .findFirst()
                .map(leagueData -> String.valueOf(leagueData.getLeague().getId()))
                .orElse(null);
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

    // Classes DTOs aninhadas da API-Sports
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
    public static class PredictionResponse {
        private String prediction;

        public PredictionResponse(String prediction) {
            this.prediction = prediction;
        }
    }

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsFixtureResponse {
        @JsonProperty("response")
        private List<FixtureData> response;
    }

    // Este DTO é para a API de LIGAS
    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsLeagueResponse {
        @JsonProperty("response")
        private List<LeagueData> response;
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

    // Este DTO para a liga em FIXTURES. É diferente da resposta da API de LIGAS.
    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueDetails {
        private Long id;
        private String name;
        private String country; // Alterado para String
    }

    // A classe DTO para a resposta da API de LIGAS
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
