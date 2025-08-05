package MestredasApostas.service;

import MestredasApostas.model.entity.Aposta;
import MestredasApostas.model.dto.ApostaDTO;
import MestredasApostas.model.enums.ApostaStatusEnum;
import MestredasApostas.repository.ApostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApostaService {

    @Autowired
    private ApostaRepository apostaRepository;

    public List<Aposta> getAllApostas() {
        return apostaRepository.findAll();
    }

    public Optional<Aposta> getApostaById(Long id) {
        return apostaRepository.findById(id);
    }

    public Aposta createAposta(ApostaDTO apostaDTO) {
        Aposta newAposta = new Aposta();
        newAposta.setJogoApiId(apostaDTO.getJogoApiId());
        newAposta.setTimeCasa(apostaDTO.getTimeCasa());
        newAposta.setTimeFora(apostaDTO.getTimeFora());
        newAposta.setLiga(apostaDTO.getLiga());
        newAposta.setDataHorario(apostaDTO.getDataHorario());
        newAposta.setPalpite(apostaDTO.getPalpite());
        newAposta.setValor(apostaDTO.getValor());
        newAposta.setOdd(apostaDTO.getOdd());

        if (apostaDTO.getStatus() != null) {
            newAposta.setStatus(apostaDTO.getStatus());
        } else {
            newAposta.setStatus(ApostaStatusEnum.PENDENTE);
        }

        newAposta.setValorCashOutRecebido(apostaDTO.getValorCashOutRecebido());

        return apostaRepository.save(newAposta);
    }

    public Aposta updateAposta(Long id, ApostaDTO apostaDTO) {
        return apostaRepository.findById(id)
                .map(aposta -> {
                    aposta.setJogoApiId(apostaDTO.getJogoApiId());
                    aposta.setTimeCasa(apostaDTO.getTimeCasa());
                    aposta.setTimeFora(apostaDTO.getTimeFora());
                    aposta.setLiga(apostaDTO.getLiga());
                    aposta.setDataHorario(apostaDTO.getDataHorario());
                    aposta.setPalpite(apostaDTO.getPalpite());
                    aposta.setValor(apostaDTO.getValor());
                    aposta.setOdd(apostaDTO.getOdd());

                    if (apostaDTO.getStatus() != null) {
                        aposta.setStatus(apostaDTO.getStatus());
                    } else {
                        aposta.setStatus(ApostaStatusEnum.PENDENTE);
                    }

                    aposta.setValorCashOutRecebido(apostaDTO.getValorCashOutRecebido());
                    return apostaRepository.save(aposta);
                })
                .orElseThrow(() -> new RuntimeException("Aposta não encontrada com ID " + id));
    }

    public void deleteAposta(Long id) {
        apostaRepository.deleteById(id);
    }
}
