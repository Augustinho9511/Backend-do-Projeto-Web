package MestredasApostas.repository;

import MestredasApostas.model.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    List<Jogo> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);
}