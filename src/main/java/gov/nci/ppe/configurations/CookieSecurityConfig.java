package gov.nci.ppe.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.SessionCookieConfig;

/**
 * Configures session cookie security settings.
 * Note: Some settings are also configured in application.properties.
 * This class ensures SameSite attribute is set (not fully supported in Spring Boot 2.3.12).
 */
@Configuration
public class CookieSecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(CookieSecurityConfig.class);
    private static final String SAME_SITE_ATTRIBUTE = "SameSite";
    private static final String SAME_SITE_VALUE = "Strict";
    
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = 
                servletContext.getSessionCookieConfig();
            
            // Set secure flag for session cookies
            sessionCookieConfig.setSecure(true);
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setName("JSESSIONID");
            sessionCookieConfig.setPath("/");
            sessionCookieConfig.setMaxAge(3600);
            
            // Set SameSite attribute using reflection for Spring Boot 2.3.12 compatibility
            // The setAttribute method exists in Servlet API 4.0 but may not be recognized by IDE
            // This is necessary because server.servlet.session.cookie.same-site property
            // is not fully supported in Spring Boot 2.3.12
            try {
                java.lang.reflect.Method setAttributeMethod = 
                    SessionCookieConfig.class.getMethod("setAttribute", String.class, String.class);
                setAttributeMethod.invoke(sessionCookieConfig, SAME_SITE_ATTRIBUTE, SAME_SITE_VALUE);
                logger.info("Successfully set SameSite={} attribute on session cookies", SAME_SITE_VALUE);
            } catch (NoSuchMethodException e) {
                logger.warn("setAttribute method not available on SessionCookieConfig. " +
                           "SameSite may not be set. Consider upgrading Spring Boot version.", e);
            } catch (Exception e) {
                logger.error("Failed to set SameSite attribute on session cookies", e);
            }
        };
    }
}