package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class CpfNotFoundException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public CpfNotFoundException() {
    super(HttpStatus.NOT_FOUND, "CPF not found", 1003);
  }
}
