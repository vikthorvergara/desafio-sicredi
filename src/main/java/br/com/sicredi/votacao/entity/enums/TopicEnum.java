package br.com.sicredi.votacao.entity.enums;

public enum TopicEnum {

  VOTES;

  public String val() {
    return this.name().toLowerCase();
  }
}
