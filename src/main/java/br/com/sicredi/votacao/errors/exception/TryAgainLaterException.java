package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class TryAgainLaterException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public TryAgainLaterException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR, "Could not communicate with external API. Please, try again later.", 1005);
  }
}
