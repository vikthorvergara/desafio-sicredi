package br.com.sicredi.votacao.entity.enums;

public enum CpfValidatorEnum {

  ABLE_TO_VOTE,
  UNABLE_TO_VOTE;

  public boolean is(String name) {
    return name.equals(this.name());
  }
}
