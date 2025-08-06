package MestredasApostas.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importe HttpStatus
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux; // Importe Flux se usar para listas

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ia")
public class PalpiteController {

    private final WebClient webClient;

    @Autowired
    public PalpiteController(WebClient webClient) {
        this.webClient = webClient;
    }

    // Endpoint para buscar países
    @GetMapping("/countries")
    public List<String> getCountries() {
        // ✅ SOLUÇÃO TEMPORÁRIA: Retornando uma lista de países predefinida.
        // Isso contorna o problema de dados incompletos da API-Sports para este endpoint.
        System.out.println("DEBUG: getCountries - Retornando lista de países predefinida.");
        return Arrays.asList(
                "Brazil", "England", "Spain", "Italy", "Germany", "France", "Portugal",
                "Argentina", "Mexico", "USA", "Netherlands", "Belgium", "Turkey"
        );

        /* // CÓDIGO ANTERIOR (comentado) - Mantenha-o para referência, mas não será executado
        Mono<ApiSportsLeagueResponse> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/leagues")
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                    clientResponse.bodyToMono(String.class).map(body -> {
                        System.err.println("ERRO API-Sports (Países) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                        return new RuntimeException("Erro da API-Sports ao buscar países: " + body);
                    }))
                .bodyToMono(ApiSportsLeagueResponse.class);

        ApiSportsLeagueResponse apiResponse = null;
        try {
            apiResponse = responseMono.block();
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao receber resposta da API-Sports para países: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        if (apiResponse == null || apiResponse.getResponse() == null) {
            System.out.println("DEBUG: getCountries - Resposta da API-Sports para países foi nula ou vazia.");
            return Collections.emptyList();
        }
        System.out.println("DEBUG: getCountries - " + apiResponse.getResponse().size() + " ligas recebidas para extrair países.");

        return apiResponse.getResponse().stream()
                .filter(leagueData -> {
                    boolean isValid = leagueData != null
                                   && leagueData.getLeague() != null
                                   && leagueData.getLeague().getCountry() != null
                                   && leagueData.getLeague().getCountry().getName() != null;
                    if (!isValid) {
                        System.out.println("DEBUG: getCountries - Item de liga com dados de país nulos ou incompletos: " + leagueData);
                    }
                    return isValid;
                })
                .map(leagueData -> leagueData.getLeague().getCountry().getName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        */
    }

    // Este método busca ligas
    @GetMapping("/leagues")
    public List<String> getLeagues(@RequestParam String country) {
        Mono<ApiSportsLeagueResponse> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/leagues")
                        .queryParam("country", country)
                        .build())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class).map(body -> {
                            System.err.println("ERRO API-Sports (Ligas) - Status: " + clientResponse.statusCode() + ", Body: " + body);
                            return new RuntimeException("Erro da API-Sports ao buscar ligas: " + body);
                        }))
                .bodyToMono(ApiSportsLeagueResponse.class);

        ApiSportsLeagueResponse apiResponse = null;
        try {
            apiResponse = responseMono.block();
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao receber resposta da API-Sports para ligas: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }

        if (apiResponse == null || apiResponse.getResponse() == null) {
            System.out.println("DEBUG: getLeagues - Resposta da API-Sports para ligas foi nula ou vazia para país: " + country);
            return Collections.emptyList();
        }
        System.out.println("DEBUG: getLeagues - " + apiResponse.getResponse().size() + " ligas recebidas da API-Sports para país: " + country);

        return apiResponse.getResponse().stream()
                .filter(leagueData -> leagueData != null && leagueData.getLeague() != null && leagueData.getLeague().getName() != null)
                .map(leagueData -> leagueData.getLeague().getName())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @GetMapping("/jogos")
    public List<JogoResponse> getJogos(
            @RequestParam String date,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String league) {

        if (country == null || country.isEmpty() || league == null || league.isEmpty()) {
            System.out.println("DEBUG: getJogos - País ou Liga não fornecidos, retornando lista vazia.");
            return Collections.emptyList();
        }

        String leagueId = getLeagueId(country, league);
        if (leagueId == null) {
            System.out.println("DEBUG: getJogos - ID da Liga não encontrado para País: " + country + ", Liga: " + league);
            return Collections.emptyList();
        }

        Mono<ApiSportsFixtureResponse> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("date", date)
                        .queryParam("league", leagueId)
                        .queryParam("season", "2024") // ATENÇÃO: Verifique esta temporada na API-Sports! Pode ser "2023" ou "2022"
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
        System.out.println("DEBUG: getJogos - " + apiResponse.getResponse().size() + " jogos recebidos da API-Sports.");

        return apiResponse.getResponse().stream()
                .map(this::mapToJogoResponse)
                .collect(Collectors.toList());
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

    // CORREÇÃO AQUI: Adicionadas verificações de nulo para as propriedades aninhadas
    private JogoResponse mapToJogoResponse(FixtureData fixtureData) {
        JogoResponse jogoResponse = new JogoResponse();

        jogoResponse.setJogoId(fixtureData.getFixture() != null ? fixtureData.getFixture().getId() : null);

        // Verificações de nulo para evitar NullPointerException e garantir que o frontend receba strings
        jogoResponse.setHomeTeam(fixtureData.getTeams() != null && fixtureData.getTeams().getHome() != null ? fixtureData.getTeams().getHome().getName() : "N/A");
        jogoResponse.setAwayTeam(fixtureData.getTeams() != null && fixtureData.getTeams().getAway() != null ? fixtureData.getTeams().getAway().getName() : "N/A");

        jogoResponse.setLeagueName(fixtureData.getLeague() != null ? fixtureData.getLeague().getName() : "N/A");
        jogoResponse.setCountryName(fixtureData.getLeague() != null && fixtureData.getLeague().getCountry() != null ? fixtureData.getLeague().getCountry().getName() : "N/A");

        return jogoResponse;
    }

    // Classes DTOs aninhadas para mapear a resposta da API-Sports
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

    // A classe LeagueDetails foi corrigida para incluir o objeto CountryDetails
    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueDetails {
        private Long id;
        private String name;
        private CountryDetails country;
    }

    // A classe CountryDetails foi adicionada
    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryDetails {
        private String name;
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

    @Getter @Setter @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueData {
        private LeagueDetails league;
    }
}
