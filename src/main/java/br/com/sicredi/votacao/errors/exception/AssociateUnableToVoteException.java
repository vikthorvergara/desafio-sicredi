package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class AssociateUnableToVoteException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public AssociateUnableToVoteException() {
    super(HttpStatus.FORBIDDEN, "Associate unable to vote", 1002);
  }
}
