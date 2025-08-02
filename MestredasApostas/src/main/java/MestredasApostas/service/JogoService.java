package MestredasApostas.service;

import MestredasApostas.model.Jogo;
import MestredasApostas.repository.JogoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class JogoService {

    private final JogoRepository jogoRepository;

    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    public List<Jogo> getAllJogos() {
        return jogoRepository.findAll();
    }

    public Optional<Jogo> findById(Long id) {
        return jogoRepository.findById(id);
    }

    public Jogo saveJogo(Jogo jogo) {
        return jogoRepository.save(jogo);
    }

    public void deleteJogo(Long id) {
        jogoRepository.deleteById(id);
    }
}