package br.com.sicredi.votacao.repository;

import br.com.sicredi.votacao.entity.dto.ResultResponse;
import br.com.sicredi.votacao.entity.dto.VoteCreateRequest;
import br.com.sicredi.votacao.entity.model.Agenda;
import reactor.core.publisher.Mono;

public interface CustomAgendaRepository {

  Mono<Boolean> existsSession(String id);

  Mono<Boolean> isSessionOpen(String id);

  Mono<Boolean> existsVote(String id, String associate);

  Mono<Agenda> updateSession(String id, Long sessionExpiration);

  Mono<Agenda> addVote(String id, VoteCreateRequest voteCreateRequest);

  Mono<ResultResponse> getVoteResult(String id);
}
