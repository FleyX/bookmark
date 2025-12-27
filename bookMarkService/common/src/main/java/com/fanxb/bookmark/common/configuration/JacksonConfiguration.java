package com.fanxb.bookmark.common.configuration;


import com.fanxb.bookmark.common.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class JacksonConfiguration {
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper() {
        log.info("[system]jackson config");
        ObjectMapper objectMapper = new ObjectMapper();
        return JsonUtil.configMapper(objectMapper);
    }

}
