package be.pxl.services.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GatewayConfiguration {

    @Bean
    @Order(-1)
    public GlobalFilter globalLoggerFilter() {
        return (exchange, chain) -> {
            log.info("Incoming Request: {} {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Response Status: {} for URL: {}",
                        exchange.getResponse().getStatusCode(),
                        exchange.getRequest().getURI());
            }));
        };
    }
}