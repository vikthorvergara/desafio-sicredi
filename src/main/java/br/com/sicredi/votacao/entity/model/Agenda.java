package br.com.sicredi.votacao.entity.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import br.com.sicredi.votacao.entity.dto.VoteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Pauta entity")
public class Agenda {

  @Id
  @Schema(description = "Identification field")
  private String id;

  @Schema(description = "Name of Agenda")
  private String name;

  @Schema(description = "Time in miliseconds of the Session Expiration")
  private Long sessionExpiration;

  @Schema(description = "List of votes")
  private List<Vote> votes;

  @Schema(description = "Result of the Agenda's votes")
  private VoteResult result;
}
