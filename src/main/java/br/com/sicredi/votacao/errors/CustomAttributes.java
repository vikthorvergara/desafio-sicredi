package br.com.sicredi.votacao.errors;

import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import br.com.sicredi.votacao.errors.exception.CustomApiException;

@Component
public class CustomAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributesMap = super.getErrorAttributes(request, options);
    Throwable throwable = getError(request);
    if (throwable instanceof CustomApiException) {
      CustomApiException customApiException = (CustomApiException) throwable;
      errorAttributesMap.put("message", customApiException.getReason());
      errorAttributesMap.put("errorCode", customApiException.getCode());
      return errorAttributesMap;
    }
    return errorAttributesMap;
  }
}
