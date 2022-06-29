package br.com.sicredi.votacao.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Result of the votes DTO")
public class VoteResult {

  @JsonProperty("Sim")
  @Schema(description = "Count of 'Sim' votes")
  private Integer positive;

  @JsonProperty("Não")
  @Schema(description = "Count of 'Não' votes")
  private Integer negative;
}
