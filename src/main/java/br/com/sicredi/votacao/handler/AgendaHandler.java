package br.com.sicredi.votacao.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import java.net.URI;
import java.time.Duration;
import java.util.Calendar;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import br.com.sicredi.votacao.entity.dto.AgendaCreateRequest;
import br.com.sicredi.votacao.entity.dto.CpfValidatorResponse;
import br.com.sicredi.votacao.entity.dto.ResultResponse;
import br.com.sicredi.votacao.entity.dto.SessionRequest;
import br.com.sicredi.votacao.entity.dto.VoteCreateRequest;
import br.com.sicredi.votacao.entity.enums.CpfValidatorEnum;
import br.com.sicredi.votacao.entity.enums.TopicEnum;
import br.com.sicredi.votacao.entity.model.Agenda;
import br.com.sicredi.votacao.errors.exception.AgendaNotFoundException;
import br.com.sicredi.votacao.errors.exception.AssociateAlreadyVotedException;
import br.com.sicredi.votacao.errors.exception.AssociateUnableToVoteException;
import br.com.sicredi.votacao.errors.exception.CpfNotFoundException;
import br.com.sicredi.votacao.errors.exception.NotValidBodyException;
import br.com.sicredi.votacao.errors.exception.TryAgainLaterException;
import br.com.sicredi.votacao.errors.exception.VotingSessionAlreadyOpenException;
import br.com.sicredi.votacao.errors.exception.VotingSessionExpiredException;
import br.com.sicredi.votacao.repository.AgendaRepository;
import br.com.sicredi.votacao.validator.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AgendaHandler {

  @Value("${env.cpf-validator}")
  private String CPF_VALIDATOR_URL;

  @Autowired
  private AgendaRepository repo;

  @Autowired
  private RequestValidator validator;

  @Autowired
  private KafkaTemplate<String, ResultResponse> kafkaTemplate;

  @Autowired
  private WebClient webClient;

  public Mono<ServerResponse> get(ServerRequest req) {
    final String id = req.pathVariable("id");
    logRequest("get Agenda by ID " + id, req);
    return repo.findById(id)
        .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON)
            .body(fromPublisher(Mono.just(res), Agenda.class)))
        .switchIfEmpty(Mono.error(AgendaNotFoundException::new));
  }

  public Mono<ServerResponse> create(ServerRequest req) {
    String id = new ObjectId().toString();
    URI uri = UriComponentsBuilder.fromPath("agenda/" + id).build().toUri();
    logRequest("create Agenda", req);
    final Mono<AgendaCreateRequest> bodyToMono =
        req.bodyToMono(AgendaCreateRequest.class)
            .flatMap(validator::validate)
            .switchIfEmpty(Mono.error(NotValidBodyException::new));
    return created(uri).contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(bodyToMono
            .map(dto -> Agenda.builder().id(id).name(dto.getName()).build())
            .flatMap(repo::save), Agenda.class));
  }

  public Mono<ServerResponse> openSession(ServerRequest req) {
    final String id = req.pathVariable("id");
    logRequest("open Agenda Session ID " + id, req);
    final Mono<SessionRequest> bodyToMono =
        req.bodyToMono(SessionRequest.class)
            .flatMap(validator::validate)
            .defaultIfEmpty(new SessionRequest());
    return bodyToMono.flatMap(sessionRequest -> validateSession(id, sessionRequest)
        .flatMap(res -> repo.updateSession(id, calculateSessionExpiration(sessionRequest.getMinutes()))
            .flatMap(agenda -> sendVoteResultMessage(id, sessionRequest.getMinutes(), agenda)))
        .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON)
            .body(fromPublisher(Mono.just(res), Agenda.class))));
  }

  public Mono<ServerResponse> vote(ServerRequest req) {
    final String id = req.pathVariable("id");
    logRequest("add Vote to Agenda ID " + id, req);
    final Mono<VoteCreateRequest> bodyToMono =
        req.bodyToMono(VoteCreateRequest.class)
            .flatMap(validator::validate)
            .switchIfEmpty(Mono.error(NotValidBodyException::new));
    return bodyToMono.flatMap(voteCreateRequest -> validateVote(id, voteCreateRequest)
        .flatMap(res -> validateAssociate(voteCreateRequest))
        .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON)
            .body(fromPublisher(repo.addVote(id, voteCreateRequest), Agenda.class))));
  }

  public Mono<ServerResponse> voteResult(ServerRequest req) {
    final String id = req.pathVariable("id");
    logRequest("get Vote Result of Agenda ID " + id, req);
    return repo.getVoteResult(id)
        .flatMap(res -> ok().contentType(MediaType.APPLICATION_JSON)
            .body(fromPublisher(Mono.just(res), ResultResponse.class)))
        .switchIfEmpty(Mono.error(AgendaNotFoundException::new));
  }

  private Long calculateSessionExpiration(int minutes) {
    Calendar currentDate = Calendar.getInstance();
    currentDate.add(Calendar.MINUTE, minutes);
    return currentDate.getTimeInMillis();
  }

  private Mono<SessionRequest> validateSession(String id, SessionRequest dto) {
    return repo.existsById(id)
        .flatMap(exists -> exists
            ? repo.existsSession(id)
                .flatMap(existsSession -> !existsSession
                    ? Mono.just(dto)
                    : Mono.error(VotingSessionAlreadyOpenException::new))
            : Mono.error(AgendaNotFoundException::new));
  }

  private Mono<VoteCreateRequest> validateVote(String id, VoteCreateRequest dto) {
    return repo.existsById(id)
        .flatMap(exists -> exists
            ? repo.isSessionOpen(id)
                .flatMap(isSessionOpen -> isSessionOpen
                    ? repo.existsVote(id, dto.getAssociate())
                        .flatMap(existsVote -> !existsVote
                            ? Mono.just(dto)
                            : Mono.error(AssociateAlreadyVotedException::new))
                    : Mono.error(VotingSessionExpiredException::new))
            : Mono.error(AgendaNotFoundException::new));
  }

  private Mono<VoteCreateRequest> validateAssociate(VoteCreateRequest dto) {
    return webClient.get().uri(CPF_VALIDATOR_URL + dto.getAssociate())
        .retrieve()
        .bodyToMono(CpfValidatorResponse.class)
            .flatMap(res -> CpfValidatorEnum.ABLE_TO_VOTE.is(res.getStatus())
                ? Mono.just(dto)
                : Mono.error(AssociateUnableToVoteException::new))
        .onErrorResume(WebClientResponseException.NotFound.class,
            notFound -> Mono.error(CpfNotFoundException::new))
        .onErrorResume(WebClientRequestException.class,
            error -> Mono.error(TryAgainLaterException::new));
  }

  private Mono<Agenda> sendVoteResultMessage(String id, int minutes, Agenda agenda) {
    final String topic = TopicEnum.VOTES.val();
    Mono.delay(Duration.ofMinutes(minutes))
        .flatMap(delay -> {
          log.info("sending kafka message in {} minutes", minutes);
          return repo.getVoteResult(id).flatMap(res -> {
            log.info("kafka message sent at topic {} and value {}", topic, res);
            kafkaTemplate.send(topic, res);
            return Mono.just(res);
          });
        }).subscribe();
    return Mono.just(agenda);
  }

  private void logRequest(String message, ServerRequest req) {
    log.info("{} - {}: {}", req.methodName(), req.path(), message);
  }
}
