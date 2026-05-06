package com.byteentropy.mesh_core.config;

import com.byteentropy.mesh_core.filter.IsoMediationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator meshRoutes(RouteLocatorBuilder builder, IsoMediationFilter mediation) {
        return builder.routes()
            .route("trading-core", r -> r.path("/trade/**")
                .uri("lb://trade-matching-core"))
            .route("legacy-rails", r -> r.path("/bank/**")
                .filters(f -> f.filter(mediation.apply(new IsoMediationFilter.Config())))
                .uri("lb://iso-adapter-core"))
            .build();
    }
}
