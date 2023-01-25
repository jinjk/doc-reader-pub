package com.utilsrv.doc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(DFSBatchConfiguration.class)
@ComponentScan(basePackages = "com.utilsrv.doc")
public class DFSConfiguration {
    @Bean
    @ConditionalOnMissingBean(BaseDocFileStatusChangeHandler.class)
    public BaseDocFileStatusChangeHandler docFileStatusChangeHandler() {
        return new BaseDocFileStatusChangeHandler();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
