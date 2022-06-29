package br.com.sicredi.votacao.errors.exception;

import org.springframework.http.HttpStatus;

public class VotingSessionExpiredException extends CustomApiException {

  private static final long serialVersionUID = 1L;

  public VotingSessionExpiredException() {
    super(HttpStatus.NOT_FOUND, "Voting session not opened or expired", 1007);
  }
}
