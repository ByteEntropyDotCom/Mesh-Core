package com.byteentropy.mesh_core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MeshCoreApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        // Verifies that the Spring context, including Security and Gateway, 
        // initializes correctly with Java 21 Virtual Threads.
        assertNotNull(context, "The Mesh-Core application context should not be null.");
    }
}