package gov.nci.ppe.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants.NewsEventType;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.NewsEvent;
import gov.nci.ppe.services.NewsEventsService;

/**
 * Unit Test class for {@link NewsController}.
 * 
 * @author PublicisSapient
 * @version 2.6;
 * @since 2022-03-24
 */
@ActiveProfiles("unittest")
@WebMvcTest(controllers = NewsController.class)
public class NewsControllerTest {

	@MockBean
	private NewsEventsService mockNewsEventsService;

	@MockBean(name = "dozerBean")
	private Mapper mockDozerBeanMapper;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetNewsEvents() throws Exception {

		List<NewsEvent> expected = new ArrayList<>();
		NewsEvent news = new NewsEvent();
		news.setContentType(NewsEventType.NEWS);
		NewsEvent event = new NewsEvent();
		event.setContentType(NewsEventType.EVENT);
		expected.add(news);
		expected.add(event);
		when(mockNewsEventsService.getActiveNewsEvents()).thenReturn(expected);
		String result = mockMvc.perform(get(UrlConstants.URL_NEWS_EVENTS)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(expected.size()))).andReturn().getResponse().getContentAsString();
		assertTrue(StringUtils.isNotBlank(result));
		verify(mockNewsEventsService).getActiveNewsEvents();
		verify(mockDozerBeanMapper, times(expected.size())).map(any(NewsEvent.class), any(Class.class));
	}

}
