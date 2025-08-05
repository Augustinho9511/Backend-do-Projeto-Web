package MestredasApostas.service;

import MestredasApostas.model.dto.PalpiteDTO;
import MestredasApostas.model.api.Jogo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PalpiteService {

    public List<PalpiteDTO> generatePalpites(List<Jogo> jogos) {
        List<PalpiteDTO> palpitesGerados = new ArrayList<>();

        if (jogos == null || jogos.isEmpty()) {
            return palpitesGerados;
        }

        final BigDecimal HOME_WIN_ODD = new BigDecimal("1.80");
        final BigDecimal DRAW_ODD = new BigDecimal("3.20");
        final BigDecimal AWAY_WIN_ODD = new BigDecimal("4.00");
        final BigDecimal DEFAULT_ODD = new BigDecimal("1.50"); // Odd padrão

        for (Jogo jogo : jogos) {
            String timeCasa = "N/A";
            String timeFora = "N/A";
            String liga = "N/A";
            LocalDateTime dataHorario = null;
            Long fixtureApiId = null;

            // Tentativa de obter dados do objeto Jogo, lidando com possíveis nulos
            if (jogo.getTeams() != null) {
                if (jogo.getTeams().getHome() != null) {
                    timeCasa = jogo.getTeams().getHome().getName();
                }
                if (jogo.getTeams().getAway() != null) {
                    timeFora = jogo.getTeams().getAway().getName();
                }
            }
            if (jogo.getLeague() != null) {
                liga = jogo.getLeague().getName();
            }
            if (jogo.getFixture() != null) {
                dataHorario = jogo.getFixture().getLocalDateTime();
                // CORREÇÃO: Usando getId() em vez de getApiId()
                fixtureApiId = jogo.getFixture().getId();
            }

            String palpiteTexto;
            BigDecimal oddSugerida;
            String analiseGolsTexto; // Variável para a análise de gols

            // Lógica de Palpite Principal
            if (jogo.getStatusCurto() != null) {
                switch (jogo.getStatusCurto().toUpperCase()) {
                    case "FT": // Full Time - Jogo terminado
                        if (jogo.getGolsCasa() != null && jogo.getGolsFora() != null) {
                            int totalGoals = jogo.getGolsCasa() + jogo.getGolsFora();
                            palpiteTexto = timeCasa + " venceu (" + jogo.getGolsCasa() + "-" + jogo.getGolsFora() + ")";
                            if (jogo.getGolsCasa() > jogo.getGolsFora()) {
                                palpiteTexto = timeCasa + " venceu (" + jogo.getGolsCasa() + "-" + jogo.getGolsFora() + ")";
                            } else if (jogo.getGolsFora() > jogo.getGolsCasa()) {
                                palpiteTexto = timeFora + " venceu (" + jogo.getGolsCasa() + "-" + jogo.getGolsFora() + ")";
                            } else {
                                palpiteTexto = "Empate (" + jogo.getGolsCasa() + "-" + jogo.getGolsFora() + ")";
                            }
                            oddSugerida = null; // Odds não são relevantes para resultados passados

                            // Análise de Gols para jogos terminados
                            analiseGolsTexto = "Total de Gols: " + totalGoals + ". ";
                            if (totalGoals > 2) {
                                analiseGolsTexto += "Over 2.5 Gols: Sim. ";
                            } else {
                                analiseGolsTexto += "Over 2.5 Gols: Não. ";
                            }
                            if (jogo.getGolsCasa() > 0 && jogo.getGolsFora() > 0) {
                                analiseGolsTexto += "Ambas Marcam: Sim.";
                            } else {
                                analiseGolsTexto += "Ambas Marcam: Não.";
                            }

                        } else {
                            palpiteTexto = "Resultado Final (Gols indisponíveis)";
                            oddSugerida = null;
                            analiseGolsTexto = "Análise de Gols indisponível.";
                        }
                        break;
                    case "NS": // Not Started - Jogo não começou
                    case "TBD": // To Be Defined - A ser definido
                    case "PST": // Postponed - Adiado
                    case "CANC": // Cancelled - Cancelado
                        // Lógica para jogos futuros ou não iniciados
                        if (fixtureApiId != null) {
                            if (fixtureApiId % 2 == 0) {
                                palpiteTexto = timeCasa + " para vencer";
                                oddSugerida = HOME_WIN_ODD;
                            } else if (fixtureApiId % 3 == 0) {
                                palpiteTexto = timeFora + " para vencer";
                                oddSugerida = AWAY_WIN_ODD;
                            } else {
                                palpiteTexto = "Empate";
                                oddSugerida = DRAW_ODD;
                            }
                        } else {
                            palpiteTexto = "Palpite Padrão: Vitória " + timeCasa;
                            oddSugerida = DEFAULT_ODD;
                        }
                        // Análise de Gols para jogos não iniciados (previsão)
                        // Esta é uma lógica simplificada, pode ser aprimorada com dados de histórico
                        if (fixtureApiId % 4 == 0) {
                            analiseGolsTexto = "Previsão: Mais de 2.5 Gols.";
                        } else if (fixtureApiId % 5 == 0) {
                            analiseGolsTexto = "Previsão: Ambas Marcam.";
                        } else {
                            analiseGolsTexto = "Previsão: Menos de 3.5 Gols.";
                        }
                        break;
                    case "LIVE": // Live - Jogo ao vivo
                    case "HT": // Half Time - Intervalo
                    case "ET": // Extra Time - Prorrogação
                    case "PEN": // Penalty Shootout - Pênaltis
                    case "BREAK": // Break - Pausa
                    case "AET": // After Extra Time
                    case "FT_PEN": // Full Time after penalties
                    case "P": // Penalties
                    case "SUSP": // Suspended
                    case "INT": // Interrupted
                    case "ABAN": // Abandoned
                    case "WO": // WalkOver
                        palpiteTexto = "Jogo " + (jogo.getStatusLongo() != null ? jogo.getStatusLongo() : "ao vivo/em andamento") + " - Análise em tempo real (não implementada)";
                        oddSugerida = DEFAULT_ODD;
                        analiseGolsTexto = "Análise de Gols em tempo real indisponível.";
                        break;
                    default:
                        palpiteTexto = "Status Desconhecido: " + (jogo.getStatusLongo() != null ? jogo.getStatusLongo() : "N/A");
                        oddSugerida = DEFAULT_ODD;
                        analiseGolsTexto = "Análise de Gols indisponível.";
                        break;
                }
            } else {
                // Se o status for nulo, ainda tenta gerar um palpite padrão
                palpiteTexto = "Palpite Padrão (Status Indisponível)";
                oddSugerida = DEFAULT_ODD;
                analiseGolsTexto = "Análise de Gols indisponível (Status nulo).";
            }

            palpitesGerados.add(new PalpiteDTO(
                    fixtureApiId,
                    timeCasa,
                    timeFora,
                    liga,
                    dataHorario,
                    palpiteTexto,
                    oddSugerida,
                    analiseGolsTexto // Passa a análise de gols
            ));
        }

        return palpitesGerados;
    }
}
