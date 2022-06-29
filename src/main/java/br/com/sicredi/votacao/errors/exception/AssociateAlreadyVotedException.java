package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class AssociateAlreadyVotedException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public AssociateAlreadyVotedException() {
    super(HttpStatus.FORBIDDEN, "Associate already voted", 1001);
  }
}
