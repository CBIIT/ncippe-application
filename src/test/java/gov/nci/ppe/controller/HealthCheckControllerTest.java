package gov.nci.ppe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	public void testIsHealthy() throws Exception {

		MvcResult result = mockMvc.perform(get("/healthcheck")).andExpect(status().isOk()).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}
}

