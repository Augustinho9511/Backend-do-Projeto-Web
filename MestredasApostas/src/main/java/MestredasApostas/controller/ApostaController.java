package MestredasApostas.controller;

import MestredasApostas.model.Aposta;
import MestredasApostas.service.ApostaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/apostas")
public class ApostaController {

    private final ApostaService apostaService;

    public ApostaController(ApostaService apostaService) {
        this.apostaService = apostaService;
    }


    @GetMapping
    public List<Aposta> getAllApostas() {
        return apostaService.getApostas();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Aposta> getApostaById(@PathVariable Long id) {
        return apostaService.findApostaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Aposta> createAposta(@RequestBody Aposta aposta) {
        try {
            Aposta novaAposta = apostaService.criarAposta(aposta);
            return ResponseEntity.ok(novaAposta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aposta> updateAposta(@PathVariable Long id, @RequestBody Aposta updatedAposta) {
        try {
            Aposta apostaAtualizada = apostaService.updateAposta(id, updatedAposta);
            return ResponseEntity.ok(apostaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAposta(@PathVariable Long id) {
        if (apostaService.findApostaById(id).isPresent()) {
            apostaService.deleteAposta(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}