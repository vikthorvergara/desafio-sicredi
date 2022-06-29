package br.com.sicredi.votacao.repository;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import br.com.sicredi.votacao.entity.dto.ResultResponse;
import br.com.sicredi.votacao.entity.dto.VoteCreateRequest;
import br.com.sicredi.votacao.entity.enums.VoteEnum;
import br.com.sicredi.votacao.entity.model.Agenda;
import br.com.sicredi.votacao.entity.model.Vote;
import reactor.core.publisher.Mono;

public class CustomAgendaRepositoryImpl implements CustomAgendaRepository {

  private final ReactiveMongoTemplate template;

  @Autowired
  public CustomAgendaRepositoryImpl(ReactiveMongoTemplate template) {
      this.template = template;
  }

  @Override
  public Mono<Boolean> existsSession(String id) {
    Query query = new Query(where("id").is(id))
        .addCriteria(where("sessionExpiration").exists(true));
    return template.exists(query, Agenda.class);
  }

  @Override
  public Mono<Boolean> isSessionOpen(String id) {
    Long currentTime = Calendar.getInstance().getTimeInMillis();
    Query query = new Query(where("id").is(id))
        .addCriteria(where("sessionExpiration").gt(currentTime));
    return template.exists(query, Agenda.class);
  }

  @Override
  public Mono<Boolean> existsVote(String id, String associate) {
    Query query = new Query(where("id").is(id))
        .addCriteria(where("votes.associate").is(associate));
    return template.exists(query, Agenda.class);
  }

  @Override
  public Mono<Agenda> updateSession(String id, Long sessionExpiration) {
    Query query = new Query(where("id").is(id));
    Update update = new Update().set("sessionExpiration", sessionExpiration);
    FindAndModifyOptions findAndModifyOptions = options().returnNew(true);
    return template.findAndModify(query, update, findAndModifyOptions, Agenda.class);
  }

  @Override
  public Mono<Agenda> addVote(String id, VoteCreateRequest voteCreateRequest) {
    Vote vote = new Vote(voteCreateRequest.getVote(), voteCreateRequest.getAssociate());
    String field = VoteEnum.POSITIVE.is(vote.getVote())
        ? VoteEnum.POSITIVE.name().toLowerCase()
        : VoteEnum.NEGATIVE.name().toLowerCase();
    Query query = new Query(where("id").is(id));
    Update update = new Update()
        .addToSet("votes", vote)
        .inc("result." + field, 1);
    FindAndModifyOptions findAndModifyOptions = options().returnNew(true);
    return template.findAndModify(query, update, findAndModifyOptions, Agenda.class);
  }

  @Override
  public Mono<ResultResponse> getVoteResult(String id) {
    Query query = new Query(where("id").is(id));
    query.fields().include("result", "name");
    return template.findOne(query, Agenda.class, "agenda")
        .flatMap(agenda -> Mono.just(new ResultResponse(agenda)));
  }
}
