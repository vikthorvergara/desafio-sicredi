package br.com.sicredi.votacao.entity.dto;

import javax.validation.constraints.Min;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request DTO for update an Agenda's Session Expiration time")
public class SessionRequest {

  @Min(value = 1)
  @Schema(description = "Time in minutes that the Session will remain open")
  private Integer minutes = 1;
}
