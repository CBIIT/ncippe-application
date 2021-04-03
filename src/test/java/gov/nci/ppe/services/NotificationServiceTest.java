package gov.nci.ppe.services;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.PortalNotificationRepository;
import gov.nci.ppe.services.impl.NotificationServiceImpl;

@ActiveProfiles("unittest")
@Tag("service")
@DisplayName("Unit Tests for NotificationServiceImpl class")
public class NotificationServiceTest {
    
    private static final String messageEnglish = "English Message";

    private static final String messageSpanish = "Spanish Message";

    private static final String subjectEnglish = "English Subject";

    private static final String subjectSpanish = "Spanish Subject";

    private static final String userId1 = UUID.randomUUID().toString();
    private static final String userId2 = UUID.randomUUID().toString();
    private static final String email1 = "user1@email.com";
    private static final String email2 = "user2@email.com";

    @Mock
    private PortalNotificationRepository mockNotificationRepo;
	
    @Mock
	private NotificationServiceConfig mockNotificationSrvConfig;

    @Mock
	private EmailLogService mockEmailService;

    @InjectMocks
    private NotificationServiceImpl classUnderTest;
    
    @Test
    public void testSendGroupNotifications_Success() {
        PortalNotification notification = new PortalNotification();
        notification.setMessageEnglish(messageEnglish);
        notification.setMessageSpanish(messageSpanish);
        notification.setSubjectEnglish(subjectEnglish);
        notification.setSubjectSpanish(subjectSpanish);

        User user1 = createUser(userId1, email1);
        User user2 = createUser(userId2, email2);

        List<User> recipientGroups = Arrays.asList(user1, user2);

        String senderId = UUID.randomUUID().toString();
        classUnderTest.sendGroupNotifications(notification, recipientGroups, senderId);

    }

    private User createUser(String userUUID, String userEmail) {
        User user = new User();
        user.setUserId(1L);
        user.setUserUUID(userUUID);
        user.setEmail(userEmail);
        return  user;
    }
}
