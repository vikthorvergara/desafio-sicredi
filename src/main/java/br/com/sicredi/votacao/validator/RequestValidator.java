package br.com.sicredi.votacao.validator;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import br.com.sicredi.votacao.errors.exception.NotValidBodyException;
import reactor.core.publisher.Mono;

@Component
public class RequestValidator {

  @Bean
  public Validator getValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }

  public <T> Mono<T> validate(T obj) {
    if (obj == null) {
      return Mono.error(new IllegalArgumentException());
    }
    Set<ConstraintViolation<T>> violations = getValidator().validate(obj);
    if (violations == null || violations.isEmpty()) {
      return Mono.just(obj);
    }
    String reason = violations.stream()
        .map(v -> v == null ? "null" : v.getPropertyPath() + ": " + v.getMessage())
        .collect(Collectors.joining(", "));
    return Mono.error(new NotValidBodyException(reason));
  }
}
