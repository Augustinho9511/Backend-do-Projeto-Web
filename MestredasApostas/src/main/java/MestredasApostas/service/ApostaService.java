package MestredasApostas.service;

import MestredasApostas.model.Aposta;
import MestredasApostas.repository.ApostaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class ApostaService {

    private final ApostaRepository apostaRepository;
    private final JogoService jogoService;

    public ApostaService(ApostaRepository apostaRepository, JogoService jogoService) {
        this.apostaRepository = apostaRepository;
        this.jogoService = jogoService;
    }

    public Optional<Aposta> findApostaById(Long id) {
        return apostaRepository.findById(id);
    }

    @Transactional
    public Aposta criarAposta(Aposta aposta) {
        jogoService.findById(aposta.getJogo().getJogoId())
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));
        return apostaRepository.save(aposta);
    }

    @Transactional
    public Aposta updateAposta(Long id, Aposta updatedAposta) {
        return apostaRepository.findById(id)
                .map(aposta -> {
                    // Atualiza apenas os campos que podem ser modificados pelo usuário
                    aposta.setValorAposta(updatedAposta.getValorAposta());
                    aposta.setPalpite(updatedAposta.getPalpite());
                    aposta.setStatus(updatedAposta.getStatus()); // Adicionado
                    aposta.setOdd(updatedAposta.getOdd()); // Adicionado
                    return apostaRepository.save(aposta);
                })
                .orElseThrow(() -> new RuntimeException("Aposta com ID " + id + " não encontrada para atualização."));
    }

    public void deleteAposta(Long id) {
        apostaRepository.deleteById(id);
    }

    public List<Aposta> getApostas() {
        return apostaRepository.findAll();
    }
}