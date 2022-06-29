package br.com.sicredi.votacao.entity.dto;

import br.com.sicredi.votacao.entity.model.Agenda;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "DTO used to show the result of the votes")
public class ResultResponse {

  @Schema(description = "Name of the Agenda")
  private String agenda;

  @Schema(description = "Result of the votes Object")
  private VoteResult result;

  public ResultResponse(Agenda agenda) {
    this.agenda = agenda.getName();
    this.result = agenda.getResult();
  }
}
