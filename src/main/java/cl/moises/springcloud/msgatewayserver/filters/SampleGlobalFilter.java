package cl.moises.springcloud.msgatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Ejecutando el filtro antes del request (Pre-filter)");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> { // filtro post-request después que se ejecuta el request se ejecuta esto para dar una respuesta
            logger.info("Ejecutando el filtro después del request (Post-filter)");

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
