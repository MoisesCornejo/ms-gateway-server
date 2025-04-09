package cl.moises.springcloud.msgatewayserver.filters.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory <ConfigurationCookie> {

    private final Logger log = LoggerFactory.getLogger(SampleCookieGatewayFilterFactory.class);

    public SampleCookieGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie configurationCookie) {
        return (exchange, chain) -> {
            log.info("Ejecutando pre gateway filter factory: {}", configurationCookie.getMessage());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                Optional.ofNullable(configurationCookie.getValue()).ifPresent(cookie -> {
                    exchange.getResponse().addCookie(ResponseCookie.from(configurationCookie.getName(), cookie).build());
                });

                log.info("Ejecutando post gateway filter factory: {}", configurationCookie.getMessage());
            }));
        };
    }
}
