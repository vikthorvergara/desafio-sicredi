package br.com.sicredi.votacao.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO used in creation of a Vote")
public class VoteCreateRequest {

  @NotBlank
  @Pattern(regexp = "Sim|Não", message = "Vote value must be 'Sim' or 'Não'.")
  @Schema(description = "Value of the vote")
  private String vote;

  @JsonAlias("cpf")
  @NotBlank
  @Schema(description = "Associate CPF")
  private String associate;
}
