package gov.nci.ppe.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.data.repository.PortalNotificationRepository;
import gov.nci.ppe.services.impl.NotificationServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class NotificationServiceTest {

	@InjectMocks
	private NotificationServiceImpl notificationService;

	@Mock
	private PortalNotificationRepository mockNotificationRepo;

	@Mock
	private NotificationServiceConfig mockNotificationSrvConfig;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGenerateUnreadReportReminderNotification() {
		notificationService.generateUnreadReportReminderNotification(7);
	}

}
