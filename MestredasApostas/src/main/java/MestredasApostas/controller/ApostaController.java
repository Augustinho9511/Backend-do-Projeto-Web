package MestredasApostas.controller;

import MestredasApostas.model.dto.ApostaDTO;
import MestredasApostas.model.entity.Aposta;
import MestredasApostas.service.ApostaService;
// Removido: import MestredasApostas.service.JogoService; // Esta linha será removida
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apostas")
public class ApostaController {

    @Autowired
    private ApostaService apostaService;

    // Removido: @Autowired private JogoService jogoService; // Esta linha será removida

    // Se você tinha um construtor, ele deve ser ajustado para não incluir JogoService
    // public ApostaController(ApostaService apostaService, JogoService jogoService) {
    //     this.apostaService = apostaService;
    //     this.jogoService = jogoService;
    // }

    @GetMapping
    public ResponseEntity<List<Aposta>> getAllApostas() {
        List<Aposta> apostas = apostaService.getAllApostas();
        return new ResponseEntity<>(apostas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aposta> getApostaById(@PathVariable Long id) {
        return apostaService.getApostaById(id)
                .map(aposta -> new ResponseEntity<>(aposta, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Aposta> createAposta(@RequestBody ApostaDTO apostaDTO) {
        Aposta createdAposta = apostaService.createAposta(apostaDTO);
        return new ResponseEntity<>(createdAposta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aposta> updateAposta(@PathVariable Long id, @RequestBody ApostaDTO apostaDTO) {
        try {
            Aposta updatedAposta = apostaService.updateAposta(id, apostaDTO);
            return new ResponseEntity<>(updatedAposta, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAposta(@PathVariable Long id) {
        apostaService.deleteAposta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
