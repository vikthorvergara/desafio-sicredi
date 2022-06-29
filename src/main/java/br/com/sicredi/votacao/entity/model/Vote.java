package br.com.sicredi.votacao.entity.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Vote entity")
public class Vote {

  @Schema(description = "Value of the vote")
  private String vote;

  @Schema(description = "Associate CPF")
  private String associate;
}
