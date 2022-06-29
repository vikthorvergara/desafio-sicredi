package br.com.sicredi.votacao.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import br.com.sicredi.votacao.entity.dto.ApiErrorResponse;
import reactor.core.publisher.Mono;

@Configuration
public class AppConfig {

  @Bean
  public WebProperties.Resources resources() {
    return new WebProperties.Resources();
  }

  @Bean
  public WebClient getWebClient() {
    return WebClient
        .builder()
        .filter(ExchangeFilterFunction.ofResponseProcessor(this::renderApiErrorResponse))
        .build();
  }

  private Mono<ClientResponse> renderApiErrorResponse(ClientResponse clientResponse) {
    if (clientResponse.statusCode().isError()) {
      return clientResponse.bodyToMono(ApiErrorResponse.class).flatMap(
          apiErrorResponse -> Mono.error(new ResponseStatusException(clientResponse.statusCode(),
              apiErrorResponse.getMessage())));
    }
    return Mono.just(clientResponse);
  }
}
