package MestredasApostas.model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Setter
@Getter
public class Jogo {

    // Getters e Setters para todos os campos
    private Long jogoId; // ID interno para a entidade Jogo, pode ser o mesmo que fixtureApiId
    private FixtureDetails fixture; // Detalhes do jogo (ID, data, status)
    private LeagueDetails league;   // Detalhes da liga
    private TeamsDetails teams;     // Detalhes dos times (casa e fora)
    private GoalsDetails goals;     // Detalhes dos gols (casa e fora)
    private String statusCurto;     // Status curto do jogo (ex: FT, NS, HT)
    private String statusLongo;     // Status longo do jogo
    private Integer golsCasa;       // Gols do time da casa
    private Integer golsFora;       // Gols do time de fora
    private VenueDetails venue;     // Detalhes do local do jogo

    // Construtor completo para facilitar a criação de objetos Jogo
    public Jogo(Long jogoId, String timeCasa, String timeFora, String liga, String dataHorarioString, Long fixtureApiId, String statusCurto, String statusLongo, Integer golsCasa, Integer golsFora, String venueName, String venueCity) {
        this.jogoId = jogoId;
        this.fixture = new FixtureDetails();
        this.fixture.setId(fixtureApiId);
        this.fixture.setDate(dataHorarioString);
        this.fixture.setStatus(new StatusDetails());
        this.fixture.getStatus().setShortStatus(statusCurto);
        this.fixture.getStatus().setLongStatus(statusLongo);

        this.league = new LeagueDetails();
        this.league.setName(liga);

        this.teams = new TeamsDetails();
        this.teams.setHome(new TeamDetails());
        this.teams.getHome().setName(timeCasa);
        this.teams.setAway(new TeamDetails());
        this.teams.getAway().setName(timeFora);

        this.goals = new GoalsDetails();
        this.goals.setHome(golsCasa);
        this.goals.setAway(golsFora);

        this.statusCurto = statusCurto;
        this.statusLongo = statusLongo;
        this.golsCasa = golsCasa;
        this.golsFora = golsFora;

        this.venue = new VenueDetails();
        this.venue.setName(venueName);
        this.venue.setCity(venueCity);
    }

    // Construtor padrão (necessário para desserialização JSON)
    public Jogo() {}

    // --- Classes Aninhadas para mapear a estrutura JSON da API-Sports ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureDetails {
        private Long id;
        private String date; // Data e hora no formato ISO 8601
        private StatusDetails status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public StatusDetails getStatus() { return status; }
        public void setStatus(StatusDetails status) { this.status = status; }

        // Método auxiliar para converter a string de data para LocalDateTime
        public LocalDateTime getLocalDateTime() {
            if (this.date == null || this.date.isEmpty()) {
                return null;
            }
            try {
                // A API retorna a data no formato ISO 8601 com fuso horário
                // Ex: "2025-08-05T00:00:00+00:00"
                // Parseamos como LocalDateTime e consideramos o offset para UTC
                return LocalDateTime.parse(this.date, DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC));
            } catch (DateTimeParseException e) {
                System.err.println("Erro ao parsear data do fixture: " + this.date + " - " + e.getMessage());
                return null;
            }
        }
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
        // Outros campos da liga podem ser adicionados aqui (ex: id, country, logo)

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
        // Outros campos do time podem ser adicionados aqui (ex: id, logo)

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
    public static class VenueDetails {
        private String name;
        private String city;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}
