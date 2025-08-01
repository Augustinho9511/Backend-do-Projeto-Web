package MestredasApostas.repository;

import MestredasApostas.model.Aposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ApostaRepository extends JpaRepository<Aposta, Long> {
}