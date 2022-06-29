package br.com.sicredi.votacao.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import br.com.sicredi.votacao.entity.model.Agenda;

@Repository
public interface AgendaRepository extends ReactiveMongoRepository<Agenda, String>, CustomAgendaRepository {
}
