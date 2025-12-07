package com.bajiezu.cloud.framework.jackson.config;

import com.bajiezu.cloud.common.util.json.databind.NumberSerializer;
import com.bajiezu.cloud.common.util.json.databind.TimestampLocalDateTimeDeserializer;
import com.bajiezu.cloud.common.util.json.databind.TimestampLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AutoConfiguration(after = JacksonAutoConfiguration.class)
@Slf4j
public class BajieCloudJacksonAutoConfiguration {

  /**
   * 从 Builder 源头定制（关键：使用 *ByType，避免 handledType 要求）
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer ldtEpochMillisCustomizer() {
    return builder -> builder
        // Long -> Number
        .serializerByType(Long.class, NumberSerializer.INSTANCE)
        .serializerByType(Long.TYPE, NumberSerializer.INSTANCE)
        // LocalDate / LocalTime
        .serializerByType(LocalDate.class, LocalDateSerializer.INSTANCE)
        .deserializerByType(LocalDate.class, LocalDateDeserializer.INSTANCE)
        .serializerByType(LocalTime.class, LocalTimeSerializer.INSTANCE)
        .deserializerByType(LocalTime.class, LocalTimeDeserializer.INSTANCE)
        // LocalDateTime < - > EpochMillis
        .serializerByType(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE)
        .deserializerByType(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);
  }

  /**
   * 以 Bean 形式暴露 Module（Boot 会自动注册到所有 ObjectMapper）
   */
  @Bean
  public Module timestampSupportModuleBean() {
    SimpleModule m = new SimpleModule("TimestampSupportModule");
    // Long -> Number，避免前端精度丢失
    m.addSerializer(Long.class, NumberSerializer.INSTANCE);
    m.addSerializer(Long.TYPE, NumberSerializer.INSTANCE);
    // LocalDate / LocalTime
    m.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
    m.addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
    m.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
    m.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
    // LocalDateTime < - > EpochMillis
    m.addSerializer(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE);
    m.addDeserializer(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);
    return m;
  }


}
