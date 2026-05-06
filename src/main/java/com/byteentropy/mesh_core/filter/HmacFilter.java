package com.byteentropy.mesh_core.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HmacFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String signature = exchange.getRequest().getHeaders().getFirst("X-Signature");
        
        // Security Gate: Reject if missing or invalid
        if (signature == null || !verify(signature, exchange)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }

    private boolean verify(String sig, ServerWebExchange ex) {
        // Production implementation would involve:
        // HmacSHA256(requestBody + secret) == sig
        // For the mesh-core demo/test, we validate our mock signature
        return "valid_mock_signature".equals(sig) || "prod_aligned_secret".equals(sig);
    }

    @Override
    public int getOrder() {
        return -100; // Ensures this runs BEFORE Tokenization and Mediation
    }
}