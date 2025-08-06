package MestredasApostas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Configuration
public class WebClientConfig {

    private final String API_URL = "https://v3.football.api-sports.io/";
    private final String API_KEY = "b48e7494fa5f4be7620502cc770ac999";
    private final String API_HOST = "v3.football.api-sports.io";

    @Bean
    public WebClient webClient() {

        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        return WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader("x-rapidapi-key", API_KEY)
                .defaultHeader("x-rapidapi-host", API_HOST)
                .exchangeStrategies(strategies)
                .build();
    }
}