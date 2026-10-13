package com.saranaresturantsystem.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WebConfigTest {

    @Test
    void corsAllowsOnlyConfiguredOrigins() throws Exception {
        WebConfig webConfig = new WebConfig("http://localhost:5173, https://360-system.vercel.app");
        WebMvcConfigurer webMvcConfigurer = webConfig.corsConfigurer();

        CorsRegistry corsRegistry = new CorsRegistry();
        webMvcConfigurer.addCorsMappings(corsRegistry);

        Method method = CorsRegistry.class.getDeclaredMethod("getCorsConfigurations");
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, CorsConfiguration> corsConfigurations =
                (Map<String, CorsConfiguration>) method.invoke(corsRegistry);

        CorsConfiguration corsConfiguration = corsConfigurations.get("/api/v1/**");

        assertEquals("http://localhost:5173", corsConfiguration.checkOrigin("http://localhost:5173"));
        assertEquals("https://360-system.vercel.app", corsConfiguration.checkOrigin("https://360-system.vercel.app"));
        assertNull(corsConfiguration.checkOrigin("https://not-allowed.example.com"));
    }
}
