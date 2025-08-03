package MestredasApostas.service;

import MestredasApostas.model.dto.ApostaDTO;
import MestredasApostas.model.entity.Aposta;
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
    public ApostaDTO criarAposta(ApostaDTO apostaDTO) {

        Aposta newAposta = new Aposta();
        newAposta.setOdd(apostaDTO.getOdd());
        newAposta.setLiga(apostaDTO.getLiga());
        newAposta.setTimeCasa(apostaDTO.getTimeCasa());
        newAposta.setTimeFora(apostaDTO.getTimeFora());
        newAposta.setDataHorario(apostaDTO.getData());
        newAposta.setPalpite(apostaDTO.getPalpite());
        newAposta.setValor(apostaDTO.getValor());
        newAposta.setValorCashOutRecebido(apostaDTO.getValorCashOutRecebido());
        newAposta.setStatus(apostaDTO.getStatus());
        apostaRepository.save(newAposta);
        return apostaDTO;
    }

    @Transactional
    public Aposta updateAposta(Long id, Aposta updatedAposta) {
        return apostaRepository.findById(id)
                .map(aposta -> {
                    aposta.setTimeCasa(updatedAposta.getTimeCasa());
                    aposta.setTimeFora(updatedAposta.getTimeFora());
                    aposta.setDataHorario(updatedAposta.getDataHorario());
                    aposta.setLiga(updatedAposta.getLiga());
                    aposta.setValor(updatedAposta.getValor());
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