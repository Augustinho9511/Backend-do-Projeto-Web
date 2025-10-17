package MestredasApostas.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class TestScraper {

    public static void main(String[] args) {
        String matchId = "14069259";
        String apiUrl = "https://api.sofascore.com/api/v1/event/" + matchId;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONObject event = jsonResponse.getJSONObject("event");

                String nomeTimeCasa = event.getJSONObject("homeTeam").getString("name");
                String nomeTimeVisitante = event.getJSONObject("awayTeam").getString("name");

                int placarCasa = event.getJSONObject("homeScore").getInt("current");
                int placarVisitante = event.getJSONObject("awayScore").getInt("current");

                System.out.println("Nome do Time da Casa: " + nomeTimeCasa);
                System.out.println("Nome do Time Visitante: " + nomeTimeVisitante);
                System.out.println("Placar: " + placarCasa + " - " + placarVisitante);
            } else {
                System.out.println("Erro ao acessar a API. Código de status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
