package cl.moises.springcloud.msgatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Ejecutando el filtro antes del request (Pre-filter)");

        // Crear un nuevo request con los headers mutados
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("token", "abcdefghi") // Aquí se agrega el header
                .build();

        // Crear un nuevo exchange con el request modificado
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            logger.info("Aquí está el filtro post response");

            // Leer el header desde el request modificado
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
            if (token != null) {
                logger.info("Token: {}", token);
            }

            Optional.ofNullable(token).ifPresent(value -> {
                logger.info("Token en la respuesta: {}", value);
                mutatedExchange.getResponse().getHeaders().add("token", value); // Agregar a la respuesta
            });

            mutatedExchange.getResponse().getCookies()
                    .add("color", ResponseCookie.from("color", "red").build());
            mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
