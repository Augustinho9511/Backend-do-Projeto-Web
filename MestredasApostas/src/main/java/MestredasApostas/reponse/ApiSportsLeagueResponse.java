package MestredasApostas.reponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiSportsLeagueResponse {

    @JsonProperty("response")
    private List<LeagueData> response;

    public List<LeagueData> getResponse() {
        return response;
    }

    public void setResponse(List<LeagueData> response) {
        this.response = response;
    }

    // Classe interna para os dados de cada liga
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueData {
        private League league;
        private Country country;
        private List<Season> seasons;

        public League getLeague() { return league; }
        public void setLeague(League league) { this.league = league; }
        public Country getCountry() { return country; }
        public void setCountry(Country country) { this.country = country; }
        public List<Season> getSeasons() { return seasons; }
        public void setSeasons(List<Season> seasons) { this.seasons = seasons; }
    }

    // Classe interna para os detalhes da liga
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class League {
        private Long id;
        private String name;
        private String type;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // Classe interna para os detalhes do país
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Country {
        private String name;
        private String code;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    // Classe interna para os detalhes da temporada
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Season {
        private Integer year;
        private String start;
        private String end;

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }
        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
    }
}