package cl.moises.springcloud.msgatewayserver.filters.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory <SampleCookieGatewayFilterFactory.ConfigurationCookie> {

    private final Logger log = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);

    public SampleCookieGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie configurationCookie) {
        return (exchange, chain) -> {

            log.info("Ejecutando pre gateway filter factory: {}", configurationCookie.message);

            // Crear un nuevo request con los headers mutados
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .build();

            // Crear un nuevo exchange con el request modificado
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {

                Optional.ofNullable(configurationCookie.value).ifPresent(cookie -> {
                    exchange.getResponse().addCookie(ResponseCookie.from(configurationCookie.name, cookie).build());
                });

                log.info("Ejecutando post gateway filter factory: {}", configurationCookie.message);
            }));
        };
    }

    public static class ConfigurationCookie {

        private String name;
        private String value;
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
