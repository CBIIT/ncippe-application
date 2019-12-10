package gov.nci.ppe.data.entity.dto;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PortalNotificationDTOTest {
	
	@InjectMocks
	private PortalNotificationDTO portalNotificationDTO;
	
	private String message;
	private Timestamp dateGenerated;
	private Long userId;
	private int viewedByUser;
	private String messageFrom;
	private String subject;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		message = "This is a test message";
		dateGenerated = new Timestamp(System.currentTimeMillis());
		userId = 10L;
		viewedByUser = 1;
		messageFrom = "JUnit Test Case class";
		subject = "Testing using JUnit";
	}
	
	@Test
	public void testSetterGetter() {
		portalNotificationDTO.setMessage(message);
		portalNotificationDTO.setGeneratedDate(dateGenerated);
		portalNotificationDTO.setUserId(userId);
		portalNotificationDTO.setViewedByUser(viewedByUser);
		portalNotificationDTO.setMessageFrom(messageFrom);
		portalNotificationDTO.setSubject(subject);
		assertEquals(message , portalNotificationDTO.getMessage() );
		assertEquals(dateGenerated , portalNotificationDTO.getDateGenerated() );
		assertEquals(userId , portalNotificationDTO.getUserId() );
		assertEquals(viewedByUser , portalNotificationDTO.getViewedByUser() );
		assertEquals(messageFrom , portalNotificationDTO.getMessageFrom() );
		assertEquals(subject , portalNotificationDTO.getSubject() );
	}

}
