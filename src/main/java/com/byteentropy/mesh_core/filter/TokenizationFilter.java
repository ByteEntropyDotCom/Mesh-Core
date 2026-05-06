package com.byteentropy.mesh_core.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenizationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Strategy: Intercept request, identify PANs, swap with Tokens from vault-core
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return -90; } // Execute after HMAC
}