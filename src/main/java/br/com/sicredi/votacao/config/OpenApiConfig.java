package br.com.sicredi.votacao.config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(info = @Info(
  title = "${springfox.documentation.info.title}",
  version = "${springfox.documentation.info.version}",
  description = "${springfox.documentation.info.description}"
))
public class OpenApiConfig {
}
