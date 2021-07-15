package gov.nci.ppe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import gov.nci.ppe.constants.UrlConstants;

/**
 * Unit Test class for {@link HealthCheckController}.
 * 
 * @author PublicisSapient
 * @version 2.4;
 * @since 2021-05-17
 */

@ActiveProfiles("unittest")
@WebMvcTest(controllers = HealthCheckController.class)
public class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealthCheck() {
        try {
            mockMvc.perform(get(UrlConstants.URL_HEALTHCHECK)).andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
