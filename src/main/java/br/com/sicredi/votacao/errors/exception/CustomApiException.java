package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.Getter;

@Getter
public abstract class CustomApiException extends ResponseStatusException {

  private static final long serialVersionUID = 1L;

  private Integer code;

  public CustomApiException(HttpStatus status, String reason, int code) {
    super(status, reason);
    this.code = code;
  }
}
