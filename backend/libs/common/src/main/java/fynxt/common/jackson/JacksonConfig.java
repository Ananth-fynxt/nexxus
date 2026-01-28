package fynxt.common.jackson;

import java.math.BigDecimal;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class JacksonConfig {

	@Bean
	JsonMapperBuilderCustomizer jacksonCustomizer() {
		return builder -> {
			builder.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
			builder.enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN);
			builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

			SimpleModule module = new SimpleModule();
			module.addSerializer(BigDecimal.class, ToStringSerializer.instance);
			builder.addModule(module);
		};
	}
}
