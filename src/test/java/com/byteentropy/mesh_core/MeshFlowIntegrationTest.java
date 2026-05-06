package com.byteentropy.mesh_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MeshFlowIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void whenRequestMissingSignature_thenReturns401Unauthorized() {
        webClient.get().uri("/trade/status")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenValidRequest_thenPassesSecurityFilter() {
        webClient.get().uri("/trade/status")
                .header("X-Signature", "valid_mock_signature")
                .exchange()
                .expectStatus().value(status -> {
                    // We expect NOT 401. 
                    // 404 or 503 means the filter let the request through!
                    assertTrue(status != 401, "Filter should have allowed the request, but got 401");
                });
    }
}