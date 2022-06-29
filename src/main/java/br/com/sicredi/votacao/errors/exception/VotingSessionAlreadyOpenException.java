package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class VotingSessionAlreadyOpenException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public VotingSessionAlreadyOpenException() {
    super(HttpStatus.BAD_REQUEST, "Voting session already opened or expired", 1006);
  }
}
