package MestredasApostas.controller;

import MestredasApostas.model.entity.Jogo;
import MestredasApostas.service.JogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    private final JogoService jogoService;

    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @GetMapping
    public List<Jogo> getAllJogos() {
        return jogoService.getAllJogos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jogo> getJogoById(@PathVariable Long id) {
        return jogoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Jogo createJogo(@RequestBody Jogo jogo) {
        return jogoService.saveJogo(jogo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJogo(@PathVariable Long id) {
        if (jogoService.findById(id).isPresent()) {
            jogoService.deleteJogo(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}