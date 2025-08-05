package MestredasApostas.controller;

import MestredasApostas.model.dto.PalpiteDTO;
import MestredasApostas.model.api.Jogo;
import MestredasApostas.service.PalpiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PalpiteController {

    @Autowired
    private PalpiteService palpiteService;

    private final WebClient webClient;

    private final String API_KEY = "b48e7494fa5f4be7620502cc770ac999"; // SUBSTITUA PELA SUA CHAVE REAL DA API-SPORTS
    private final String API_URL = "https://v3.football.api-sports.io";
    private final String API_HOST = "v3.football.api-sports.io";

    public PalpiteController() {
        this.webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("x-rapidapi-key", API_KEY)
                .defaultHeader("x-rapidapi-host", API_HOST)
                .build();
    }

    /**
     * Endpoint para listar todos os jogos de uma data específica para a funcionalidade de IA.
     * Não gera palpites, apenas retorna os dados brutos dos jogos.
     */
    @GetMapping("/ia/jogos") // NOVO CAMINHO: Alterado de "/jogos" para "/ia/jogos" para evitar conflito
    public ResponseEntity<List<Jogo>> getJogosPorData(
            @RequestParam(required = false) String date
    ) {
        String finalDate = date;
        if (finalDate == null || finalDate.isEmpty()) {
            finalDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        try {
            String finalDate1 = finalDate;
            Mono<ApiSportsFixtureResponse> responseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fixtures")
                            .queryParam("date", finalDate1)
                            .build())
                    .retrieve()
                    .bodyToMono(ApiSportsFixtureResponse.class);

            ApiSportsFixtureResponse apiResponse = responseMono.block();

            if (apiResponse != null && apiResponse.getResponse() != null) {
                // LOG: Tamanho da resposta bruta da API
                System.out.println("LOG: API-Sports raw response size for /fixtures on " + finalDate + ": " + apiResponse.getResponse().size());

                List<Jogo> jogosDaApiExterna = apiResponse.getResponse().stream()
                        .map(fixtureData -> {
                            // LOG: Detalhes básicos de cada fixture antes do mapeamento
                            // System.out.println("LOG: Processing fixture ID: " + fixtureData.getFixture().getId() +
                            //                    ", Home: " + fixtureData.getTeams().getHome().getName() +
                            //                    ", Away: " + fixtureData.getTeams().getAway().getName() +
                            //                    ", Status: " + (fixtureData.getFixture().getStatus() != null ? fixtureData.getFixture().getStatus().getShortStatus() : "N/A"));

                            Long fixtureApiId = fixtureData.getFixture().getId();
                            String homeTeamName = fixtureData.getTeams().getHome().getName();
                            String awayTeamName = fixtureData.getTeams().getAway().getName();
                            String leagueName = fixtureData.getLeague().getName();
                            String fixtureDateString = fixtureData.getFixture().getDate();

                            String statusShort = fixtureData.getFixture().getStatus() != null ? fixtureData.getFixture().getStatus().getShortStatus() : null;
                            String statusLong = fixtureData.getFixture().getStatus() != null ? fixtureData.getFixture().getStatus().getLongStatus() : null;
                            Integer homeGoals = fixtureData.getGoals() != null ? fixtureData.getGoals().getHome() : null;
                            Integer awayGoals = fixtureData.getGoals() != null ? fixtureData.getGoals().getAway() : null;
                            String venueName = fixtureData.getVenue() != null ? fixtureData.getVenue().getName() : null;
                            String venueCity = fixtureData.getVenue() != null ? fixtureData.getVenue().getCity() : null;

                            return new Jogo(
                                    fixtureApiId,
                                    homeTeamName,
                                    awayTeamName,
                                    leagueName,
                                    fixtureDateString,
                                    fixtureApiId,
                                    statusShort,
                                    statusLong,
                                    homeGoals,
                                    awayGoals,
                                    venueName,
                                    venueCity
                            );
                        })
                        .collect(Collectors.toList());

                // LOG: Número de objetos Jogo retornados ao frontend
                System.out.println("LOG: Number of Jogo objects returned to frontend for /ia/jogos: " + jogosDaApiExterna.size());
                return new ResponseEntity<>(jogosDaApiExterna, HttpStatus.OK);
            } else {
                System.out.println("LOG: API-Sports response for /fixtures was null or empty for " + finalDate + ".");
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar jogos da API externa: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para gerar palpites para um jogo específico.
     * Requer o ID do jogo.
     */
    @GetMapping("/palpitesIA")
    public ResponseEntity<List<PalpiteDTO>> getPalpitesIA(
            @RequestParam(required = false) String date,
            @RequestParam Long gameId
    ) {
        String finalDate = date;
        if (finalDate == null || finalDate.isEmpty()) {
            finalDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        List<Jogo> jogosParaPalpite = new ArrayList<>();

        try {
            System.out.println("LOG: Request to /palpitesIA for gameId: " + gameId + " on date: " + finalDate);

            // NOVO: Busca o jogo específico DIRETAMENTE pela API usando o ID
            Mono<ApiSportsFixtureResponse> specificFixtureResponseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fixtures")
                            .queryParam("id", gameId) // Busca diretamente pelo ID do jogo
                            .build())
                    .retrieve()
                    .bodyToMono(ApiSportsFixtureResponse.class);

            ApiSportsFixtureResponse specificFixtureApiResponse = specificFixtureResponseMono.block();

            if (specificFixtureApiResponse != null && specificFixtureApiResponse.getResponse() != null && !specificFixtureApiResponse.getResponse().isEmpty()) {
                // Deve haver apenas um fixture se encontrado por ID
                FixtureData fixtureData = specificFixtureApiResponse.getResponse().get(0);
                System.out.println("LOG: API-Sports raw response size for specific fixture (id " + gameId + "): " + specificFixtureApiResponse.getResponse().size());
                System.out.println("LOG: Processing specific fixture ID: " + fixtureData.getFixture().getId() +
                        ", Home: " + fixtureData.getTeams().getHome().getName() +
                        ", Away: " + fixtureData.getTeams().getAway().getName() +
                        ", Status: " + (fixtureData.getFixture().getStatus() != null ? fixtureData.getFixture().getStatus().getShortStatus() : "N/A"));

                Long fixtureApiId = fixtureData.getFixture().getId();
                String homeTeamName = fixtureData.getTeams().getHome().getName();
                String awayTeamName = fixtureData.getTeams().getAway().getName();
                String leagueName = fixtureData.getLeague().getName();
                String fixtureDateString = fixtureData.getFixture().getDate();

                String statusShort = fixtureData.getFixture().getStatus() != null ? fixtureData.getFixture().getStatus().getShortStatus() : null;
                String statusLong = fixtureData.getFixture().getStatus() != null ? fixtureData.getFixture().getStatus().getLongStatus() : null;
                Integer homeGoals = fixtureData.getGoals() != null ? fixtureData.getGoals().getHome() : null;
                Integer awayGoals = fixtureData.getGoals() != null ? fixtureData.getGoals().getAway() : null;
                String venueName = fixtureData.getVenue() != null ? fixtureData.getVenue().getName() : null;
                String venueCity = fixtureData.getVenue() != null ? fixtureData.getVenue().getCity() : null;

                jogosParaPalpite.add(new Jogo(
                        fixtureApiId,
                        homeTeamName,
                        awayTeamName,
                        leagueName,
                        fixtureDateString,
                        fixtureApiId,
                        statusShort,
                        statusLong,
                        homeGoals,
                        awayGoals,
                        venueName,
                        venueCity
                ));
            } else {
                System.out.println("LOG: API-Sports response for specific fixture (id " + gameId + ") was null or empty.");
            }

            // Chama o serviço para gerar o palpite para o jogo específico
            List<PalpiteDTO> palpites = palpiteService.generatePalpites(jogosParaPalpite);

            System.out.println("LOG: Number of PalpiteDTOs generated for gameId " + gameId + ": " + palpites.size());
            if (!palpites.isEmpty()) {
                System.out.println("LOG: Generated Palpite: " + palpites.get(0).getPalpite() + ", Odd: " + palpites.get(0).getOdd());
            }

            return new ResponseEntity<>(palpites, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erro ao buscar jogo da API externa ou gerar palpite: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- CLASSES AUXILIARES PARA MAPEAR A RESPOSTA JSON DA API-SPORTS ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApiSportsFixtureResponse {
        @JsonProperty("response")
        private List<FixtureData> response;

        public List<FixtureData> getResponse() { return response; }
        public void setResponse(List<FixtureData> response) { this.response = response; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureData {
        private FixtureDetails fixture;
        private LeagueDetails league;
        private TeamsDetails teams;
        private GoalsDetails goals;
        private ScoreDetails score;
        private VenueDetails venue;

        public FixtureDetails getFixture() { return fixture; }
        public void setFixture(FixtureDetails fixture) { this.fixture = fixture; }
        public LeagueDetails getLeague() { return league; }
        public void setLeague(LeagueDetails league) { this.league = league; }
        public TeamsDetails getTeams() { return teams; }
        public void setTeams(TeamsDetails teams) { this.teams = teams; }
        public GoalsDetails getGoals() { return goals; }
        public void setGoals(GoalsDetails goals) { this.goals = goals; }
        public ScoreDetails getScore() { return score; }
        public void setScore(ScoreDetails score) { this.score = score; }
        public VenueDetails getVenue() { return venue; }
        public void setVenue(VenueDetails venue) { this.venue = venue; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureDetails {
        private Long id;
        private String date;
        private StatusDetails status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public StatusDetails getStatus() { return status; }
        public void setStatus(StatusDetails status) { this.status = status; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusDetails {
        @JsonProperty("long")
        private String longStatus;
        @JsonProperty("short")
        private String shortStatus;
        private Integer elapsed;

        public String getLongStatus() { return longStatus; }
        public void setLongStatus(String longStatus) { this.longStatus = longStatus; }
        public String getShortStatus() { return shortStatus; }
        public void setShortStatus(String shortStatus) { this.shortStatus = shortStatus; }
        public Integer getElapsed() { return elapsed; }
        public void setElapsed(Integer elapsed) { this.elapsed = elapsed; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueDetails {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamsDetails {
        private TeamDetails home;
        private TeamDetails away;

        public TeamDetails getHome() { return home; }
        public void setHome(TeamDetails home) { this.home = home; }
        public TeamDetails getAway() { return away; }
        public void setAway(TeamDetails away) { this.away = away; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDetails {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoalsDetails {
        private Integer home;
        private Integer away;

        public Integer getHome() { return home; }
        public void setHome(Integer home) { this.home = home; }
        public Integer getAway() { return away; }
        public void setAway(Integer away) { this.away = away; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ScoreDetails {
        private GoalsDetails halftime;
        private GoalsDetails fulltime;
        private GoalsDetails extratime;
        private GoalsDetails penalty;

        public GoalsDetails getHalftime() { return halftime; }
        public void setHalftime(GoalsDetails halftime) { this.halftime = halftime; }
        public GoalsDetails getFulltime() { return fulltime; }
        public void setFulltime(GoalsDetails fulltime) { this.fulltime = fulltime; }
        public GoalsDetails getExtratime() { return extratime; }
        public void setExtratime(GoalsDetails extratime) { this.extratime = extratime; }
        public GoalsDetails getPenalty() { return penalty; }
        public void setPenalty(GoalsDetails penalty) { this.penalty = penalty; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VenueDetails {
        private String name;
        private String city;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}
