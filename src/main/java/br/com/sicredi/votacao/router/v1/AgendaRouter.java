package br.com.sicredi.votacao.router.v1;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import br.com.sicredi.votacao.entity.dto.AgendaCreateRequest;
import br.com.sicredi.votacao.entity.dto.SessionRequest;
import br.com.sicredi.votacao.entity.dto.VoteCreateRequest;
import br.com.sicredi.votacao.entity.dto.VoteResult;
import br.com.sicredi.votacao.entity.model.Agenda;
import br.com.sicredi.votacao.handler.AgendaHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class AgendaRouter {

  @Bean
  @RouterOperations({
      @RouterOperation(path = "/v1/agenda/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE},
      method = RequestMethod.GET,
      beanClass = AgendaHandler.class,
      beanMethod = "get",
      operation = @Operation(operationId = "get",
          responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(schema = @Schema(implementation = Agenda.class))),
            @ApiResponse(responseCode = "404", description = "Agenda not found by given id")
          })),
      @RouterOperation(path = "/v1/agenda",
          produces = {MediaType.APPLICATION_JSON_VALUE},
          method = RequestMethod.POST,
          beanClass = AgendaHandler.class,
          beanMethod = "create",
          operation = @Operation(operationId = "create",
              responses = {@ApiResponse(responseCode = "200", description = "Successful operation",
                  content = @Content(schema = @Schema(implementation = Agenda.class)))},
              requestBody = @RequestBody(
                  content = @Content(schema = @Schema(implementation = AgendaCreateRequest.class)))
          )),
      @RouterOperation(path = "/v1/agenda/{id}/vote",
          produces = {MediaType.APPLICATION_JSON_VALUE},
          method = RequestMethod.POST,
          beanClass = AgendaHandler.class,
          beanMethod = "vote",
          operation = @Operation(operationId = "vote",
              responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Agenda.class))),
                @ApiResponse(responseCode = "400", description = "Bad request"),
                @ApiResponse(responseCode = "400", description = "Associate document is not valid"),
                @ApiResponse(responseCode = "403", description = "Associate already voted"),
                @ApiResponse(responseCode = "403", description = "Associate unable to vote"),
                @ApiResponse(responseCode = "404", description = "Agenda not found by given id"),
                @ApiResponse(responseCode = "404", description = "Associate document not found"),
                @ApiResponse(responseCode = "404", description = "Voting session not opened or expired")
              },
              requestBody = @RequestBody(content = @Content(
                  schema = @Schema(implementation = VoteCreateRequest.class))))),
      @RouterOperation(path = "/v1/agenda/{id}/open-session",
          produces = {MediaType.APPLICATION_JSON_VALUE},
          method = RequestMethod.PATCH,
          beanClass = AgendaHandler.class,
          beanMethod = "openSession",
          operation = @Operation(operationId = "openSession",
              responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Agenda.class))),
                @ApiResponse(responseCode = "400", description = "Bad request"),
                @ApiResponse(responseCode = "400", description = "Voting session already opened or expired"),
                @ApiResponse(responseCode = "404", description = "Agenda not found by given id")
              },
              requestBody = @RequestBody(content = @Content(
                  schema = @Schema(implementation = SessionRequest.class))))),
      @RouterOperation(path = "/v1/agenda/{id}/result",
          produces = {MediaType.APPLICATION_JSON_VALUE},
          method = RequestMethod.GET,
          beanClass = AgendaHandler.class,
          beanMethod = "voteResult",
          operation = @Operation(operationId = "voteResult",
              responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = VoteResult.class))),
                @ApiResponse(responseCode = "404", description = "Agenda not found by given id")
              }))
  })
  RouterFunction<ServerResponse> routerFunctionV1(AgendaHandler handler) {
    return RouterFunctions.route()
        .GET("/v1/agenda/{id}", handler::get)
        .POST("/v1/agenda", handler::create)
        .POST("/v1/agenda/{id}/vote", handler::vote)
        .PATCH("/v1/agenda/{id}/open-session", handler::openSession)
        .GET("/v1/agenda/{id}/result", handler::voteResult)
        .build();
  }
}
