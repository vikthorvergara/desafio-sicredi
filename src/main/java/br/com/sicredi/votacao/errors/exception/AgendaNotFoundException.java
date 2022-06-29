package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class AgendaNotFoundException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public AgendaNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Agenda not found by given id", 1000);
  }
}
