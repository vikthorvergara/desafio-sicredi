package br.com.sicredi.votacao.entity.enums;

public enum VoteEnum {

  POSITIVE("Sim"),
  NEGATIVE("Não");

  private String value;

  VoteEnum(String value) {
    this.value = value;
  }

  public boolean is(String value) {
    return this.value.equals(value);
  }
}
