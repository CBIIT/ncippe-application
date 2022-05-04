package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.constants.CommonConstants.NewsEventType;
import gov.nci.ppe.data.entity.NewsEvent;
import gov.nci.ppe.data.repository.NewsEventsRepository;
import gov.nci.ppe.services.impl.NewsEventsServiceImpl;

/**
 * Unit Test class for {@link NewsEventsServiceImpl}.
 * 
 * @author PublicisSapient
 * @version 2.4;
 * @since 2021-05-17
 */

@ActiveProfiles("unittest")
@Tag("service")
@DisplayName("Unit tests for NewsEventsServiceImpl class")
@ExtendWith(MockitoExtension.class)
public class NewsEventsServiceTest {

	@Mock
	private NewsEventsRepository newsEventsRepository;

	@InjectMocks
	private NewsEventsServiceImpl classUnderTest;

	@Test
	public void testGetActiveNewsEvents() {

		List<NewsEvent> expected = new ArrayList<>();
		NewsEvent news = new NewsEvent();
		news.setContentType(NewsEventType.NEWS);
		expected.add(news);

		NewsEvent event = new NewsEvent();
		event.setContentType(NewsEventType.EVENT);
		expected.add(event);

		when(newsEventsRepository.findByExpirationDateAfter(any(LocalDateTime.class))).thenReturn(expected);
		List<NewsEvent> result = classUnderTest.getActiveNewsEvents();

		assertNotNull(result);
		assertFalse(result.isEmpty());
		verify(newsEventsRepository).findByExpirationDateAfter(any(LocalDateTime.class));

	}
}
