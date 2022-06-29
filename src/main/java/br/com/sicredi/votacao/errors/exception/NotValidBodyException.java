package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class NotValidBodyException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public NotValidBodyException() {
    this("Not valid body request");
  }

  public NotValidBodyException(String reason) {
    super(HttpStatus.BAD_REQUEST, reason, 1004);
  }
}
