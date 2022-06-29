package br.com.sicredi.votacao.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import br.com.sicredi.votacao.entity.dto.ResultResponse;

@Configuration
public class KafkaProducerConfig {

  @Value("${kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${kafka.sasl-jaas-config}")
  private String saslJaasConfig;

  @Bean
  public ProducerFactory<String, ResultResponse> resultResponseConfig() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 7000);
    configProps.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
    configProps.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
    configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
    return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), new JsonSerializer<ResultResponse>());
  }

  @Bean
  public KafkaTemplate<String, ResultResponse> getKafkaTemplate() {
    return new KafkaTemplate<>(resultResponseConfig());
  }
}
