package br.com.sicredi.votacao.entity.dto;

import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO used as request to create an Agenda")
public class AgendaCreateRequest {

  @NotBlank
  @Schema(description = "Name of the new Agenda")
  private String name;
}
