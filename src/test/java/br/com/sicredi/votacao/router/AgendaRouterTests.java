package br.com.sicredi.votacao.router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.Calendar;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import br.com.sicredi.votacao.entity.dto.ResultResponse;
import br.com.sicredi.votacao.entity.dto.SessionRequest;
import br.com.sicredi.votacao.entity.dto.VoteCreateRequest;
import br.com.sicredi.votacao.entity.model.Agenda;
import br.com.sicredi.votacao.repository.AgendaRepository;
import br.com.sicredi.votacao.util.EntityCreator;
import reactor.core.publisher.Mono;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AgendaRouterTests {

  @MockBean
  private AgendaRepository repo;

  private WebTestClient client;

  @BeforeEach
  void setUp(ApplicationContext context) {
    client = WebTestClient.bindToApplicationContext(context).build();
  }

  @Test
  @DisplayName("get retrieves Agenda when successful")
  void get_ReturnsAgenda_WhenSuccessful() {
    Agenda agenda = EntityCreator.createValidAgenda();
    when(repo.findById(anyString())).thenReturn(Mono.just(agenda));
    client.get()
        .uri("/v1/agenda/{id}", agenda.getId())
        .exchange()
        .expectStatus().isOk()
        .expectBody(Agenda.class)
        .isEqualTo(agenda);
  }

  @Test
  @DisplayName("get returns Mono error (not found) when Agenda is not found by ID")
  void get_ReturnsNotFound_WhenAgendaIsNotFound() {
    when(repo.findById(anyString())).thenReturn(Mono.empty());
    client.get()
        .uri("/v1/agenda/{id}", "agenda-id")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("save creates Agenda when successful")
  void save_CreatesAgenda_WhenSuccessful() {
    Agenda agenda = EntityCreator.createValidAgenda();
    when(repo.save(any())).thenReturn(Mono.just(agenda));
    client.post()
        .uri("/v1/agenda")
        .body(Mono.just(agenda), Agenda.class)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Agenda.class)
        .isEqualTo(agenda);
  }

  @Test
  @DisplayName("save returns Mono error (bad request) when request body sent is empty")
  void save_ReturnsBadRequest_WhenEmptyMonoIsSent() {
    when(repo.save(any())).thenReturn(Mono.empty());
    client.post()
        .uri("/v1/agenda")
        .body(Mono.empty(), Agenda.class)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("update (open session) Agenda's session expiration when successful")
  void update_OpenAgendaSession_WhenSuccessful() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    SessionRequest body = EntityCreator.createValidAgendaSessionRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.existsSession(anyString())).thenReturn(Mono.just(false));
    when(repo.updateSession(anyString(), anyLong())).thenReturn(Mono.just(agenda));
    client.patch()
        .uri("/v1/agenda/{id}/open-session", agenda.getId())
        .body(Mono.just(body), SessionRequest.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Agenda.class)
        .value(res -> Assertions
            .assertThat(res.getSessionExpiration())
            .isGreaterThan(Calendar.getInstance().getTimeInMillis()));
  }

  @Test
  @DisplayName("update (open session) returns Mono error (not found) when Agenda is not found by ID")
  void update_RetrunsNotFound_WhenAgendaIsNotFound() {
    Agenda agenda = EntityCreator.createValidAgenda();
    SessionRequest body = EntityCreator.createValidAgendaSessionRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(false));
    client.patch()
        .uri("/v1/agenda/{id}/open-session", agenda.getId())
        .body(Mono.just(body), SessionRequest.class)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("update (open session) returns Mono error (bad request) when minutes field is zero")
  void update_RetrunsBadRequest_WhenInvalidRequestBodyIsSent() {
    Agenda agenda = EntityCreator.createValidAgenda();
    SessionRequest body = EntityCreator.createInvalidAgendaSessionRequest();
    client.patch()
        .uri("/v1/agenda/{id}/open-session", agenda.getId())
        .body(Mono.just(body), SessionRequest.class)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("update (open session) returns Mono error (bad request) when voting session already opened or expired")
  void update_RetrunsBadRequest_WhenAgendaSessionIsAlreadyOpen() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    SessionRequest body = EntityCreator.createValidAgendaSessionRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.existsSession(anyString())).thenReturn(Mono.just(true));
    when(repo.findById(anyString())).thenReturn(Mono.just(agenda));
    client.patch()
        .uri("/v1/agenda/{id}/open-session", agenda.getId())
        .body(Mono.just(body), SessionRequest.class)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }

  @Test
  @DisplayName("save Vote creates Agenda vote when successful")
  void saveVote_CreatesAgendaVote_WhenSuccessful() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    agenda.setVotes(List.of(EntityCreator.createValidVote()));
    VoteCreateRequest body = EntityCreator.createValidVoteCreateRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.isSessionOpen(anyString())).thenReturn(Mono.just(true));
    when(repo.existsVote(anyString(), anyString())).thenReturn(Mono.just(false));
    when(repo.addVote(anyString(), any())).thenReturn(Mono.just(agenda));
    client.post()
        .uri("/v1/agenda/{id}/vote", agenda.getId())
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Agenda.class)
        .isEqualTo(agenda);
  }

  @Test
  @DisplayName("save Vote returns Mono error (bad request) when request body is invalid")
  void saveVote_RetrunsBadRequest_WhenInvalidRequestBodyIsSent() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    VoteCreateRequest body = EntityCreator.createInvalidVoteCreateRequest();
    client.post()
        .uri("/v1/agenda/{id}/vote", agenda.getId())
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo(400);
  }


  @Test
  @DisplayName("save Vote returns Mono error (forbidden) when associate document is invalid")
  void saveVote_ReturnsBadRequest_WhenInvalidCpf() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    agenda.setVotes(List.of(EntityCreator.createValidVote()));
    VoteCreateRequest body = EntityCreator.createInvalidAssociateVoteCreateRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.isSessionOpen(anyString())).thenReturn(Mono.just(true));
    when(repo.existsVote(anyString(), anyString())).thenReturn(Mono.just(false));
    client.post()
        .uri("/v1/agenda/{id}/vote", agenda.getId())
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isForbidden()
        .expectBody()
        .jsonPath("$.status").isEqualTo(403);
  }

  @Test
  @DisplayName("save Vote returuns Mono error (forbidden) when associate already have a registered vote")
  void saveVote_ReturnsForbidden_WhenAssociateAlreadyVoted() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    agenda.setVotes(List.of(EntityCreator.createValidVote()));
    VoteCreateRequest body = EntityCreator.createValidVoteCreateRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.isSessionOpen(anyString())).thenReturn(Mono.just(true));
    when(repo.existsVote(anyString(), anyString())).thenReturn(Mono.just(true));
    client.post()
        .uri("/v1/agenda/{id}/vote", agenda.getId())
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isForbidden()
        .expectBody()
        .jsonPath("$.status").isEqualTo(403);
  }

  @Test
  @DisplayName("save Vote returns Mono error (not found) when Agenda is not found by ID")
  void saveVote_ReturnsNotFound_WhenAgendaIsNotFound() {
    VoteCreateRequest body = EntityCreator.createValidVoteCreateRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(false));
    client.post()
        .uri("/v1/agenda/{id}/vote", "agenda-id")
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("save Vote returns Mono error (not found) when Associate is not found by external resource")
  void saveVote_ReturnsNotFound_WhenAssociateNotFound() {
    Agenda agenda = EntityCreator.createValidAgendaWithOpenSession();
    agenda.setVotes(List.of(EntityCreator.createValidVote()));
    VoteCreateRequest body = EntityCreator.createInexistentAssociateVoteCreateRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.isSessionOpen(anyString())).thenReturn(Mono.just(true));
    when(repo.existsVote(anyString(), anyString())).thenReturn(Mono.just(false));
    client.post()
        .uri("/v1/agenda/{id}/vote", agenda.getId())
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("save Vote returns Mono error (not found) when Agenda session is not open for voting")
  void saveVote_ReturnsNotFound_WhenAgendaSessionIsNotFound() {
    Agenda agenda = EntityCreator.createValidAgenda();
    VoteCreateRequest body = EntityCreator.createValidVoteCreateRequest();
    when(repo.existsById(anyString())).thenReturn(Mono.just(true));
    when(repo.isSessionOpen(anyString())).thenReturn(Mono.just(false));
    client.post()
        .uri("/v1/agenda/{id}/vote", agenda.getId())
        .body(Mono.just(body), VoteCreateRequest.class)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }

  @Test
  @DisplayName("get result returns the current voting result when successful")
  void getResult_ReturnsResult_WhenSuccessful() {
    ResultResponse agendaResult = new ResultResponse(EntityCreator.createValidAgenda());
    when(repo.getVoteResult(anyString())).thenReturn(Mono.just(agendaResult));
    client.get()
        .uri("/v1/agenda/{id}/result", "agenda-id")
        .exchange()
        .expectStatus().isOk()
        .expectBody(ResultResponse.class)
        .isEqualTo(agendaResult);
  }

  @Test
  @DisplayName("get result returns Mono error (not found) when Agenda is not found by ID")
  void getResult_ReturnsNotFound_WhenAgendaNotFound() {
    when(repo.getVoteResult(anyString())).thenReturn(Mono.empty());
    client.get()
        .uri("/v1/agenda/{id}/result", "agenda-id")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo(404);
  }
}
