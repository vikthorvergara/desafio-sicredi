package br.com.sicredi.votacao.util;

import java.util.Calendar;
import br.com.sicredi.votacao.entity.dto.SessionRequest;
import br.com.sicredi.votacao.entity.dto.VoteCreateRequest;
import br.com.sicredi.votacao.entity.dto.VoteResult;
import br.com.sicredi.votacao.entity.model.Agenda;
import br.com.sicredi.votacao.entity.model.Vote;

public class EntityCreator {

  public static Agenda createAgendaToBeSaved() {
    return Agenda.builder()
        .name("Pauta Teste")
        .build();
  }

  public static Agenda createValidAgenda() {
    return Agenda.builder()
        .id("pauta_teste")
        .name("Pauta Teste")
        .build();
  }

  public static Agenda createAgendaWithVoteResult() {
    return Agenda.builder()
        .id("pauta_teste")
        .name("Pauta Teste")
        .result(VoteResult.builder().positive(10).negative(5).build())
        .build();
  }

  public static Vote createValidVote() {
    return Vote.builder().associate("26237436570").vote("Sim").build();
  }

  public static Agenda createValidAgendaWithOpenSession() {
    Calendar currentDate = Calendar.getInstance();
    currentDate.add(Calendar.MINUTE, 15);
    return Agenda.builder()
        .id("pauta_teste")
        .name("Pauta Teste")
        .sessionExpiration(currentDate.getTimeInMillis())
        .build();
  }

  public static SessionRequest createValidAgendaSessionRequest() {
    return new SessionRequest(15);
  }

  public static SessionRequest createInvalidAgendaSessionRequest() {
    return new SessionRequest(0);
  }

  public static VoteCreateRequest createValidVoteCreateRequest() {
    return new VoteCreateRequest("Sim", "26237436570");
  }

  public static VoteCreateRequest createInvalidAssociateVoteCreateRequest() {
    return new VoteCreateRequest("Sim", "62289608068");
  }

  public static VoteCreateRequest createInexistentAssociateVoteCreateRequest() {
    return new VoteCreateRequest("Sim", "99999999999");
  }

  public static VoteCreateRequest createInvalidVoteCreateRequest() {
    return new VoteCreateRequest();
  }
}
