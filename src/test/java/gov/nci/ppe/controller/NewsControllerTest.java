package gov.nci.ppe.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.NewsEvent;
import gov.nci.ppe.services.NewsEventsService;

@ActiveProfiles("unittest")
@WebMvcTest(controllers = NewsController.class)
public class NewsControllerTest {

	@MockBean
	private NewsEventsService mockNewsEventsService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetNewsEvents() throws Exception {

		List<NewsEvent> expected = new ArrayList<>();
		NewsEvent news = new NewsEvent();
		NewsEvent event = new NewsEvent();
		expected.add(news);
		expected.add(event);
		when(mockNewsEventsService.getActiveNewsEvents()).thenReturn(expected);
		String result = mockMvc.perform(get(UrlConstants.URL_NEWS_EVENTS)).andExpect(status().isOk())
				.andExpect(jsonPath("$.news").isNotEmpty()).andExpect(jsonPath("$.events").isNotEmpty()).andReturn()
				.getResponse().getContentAsString();
		assertTrue(StringUtils.isNotBlank(result));
		verify(mockNewsEventsService).getActiveNewsEvents();

	}

}
