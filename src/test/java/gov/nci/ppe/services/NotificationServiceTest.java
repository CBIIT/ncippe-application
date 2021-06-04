package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.GroupNotificationRequest;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.GroupNotificationRequestRepository;
import gov.nci.ppe.data.repository.PortalNotificationRepository;
import gov.nci.ppe.data.repository.RoleRepository;
import gov.nci.ppe.services.impl.NotificationServiceImpl;

@ActiveProfiles("unittest")
@Tag("service")
@DisplayName("Unit Tests for NotificationServiceImpl class")
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	private static final String messageEnglish = "English Message";

	private static final String messageSpanish = "Spanish Message";

	private static final String subjectEnglish = "English Subject";

	private static final String subjectSpanish = "Spanish Subject";

	private static final String userId1 = UUID.randomUUID().toString();
	private static final String userId2 = UUID.randomUUID().toString();
	private static final String email1 = "user1@email.com";
	private static final String email2 = "user2@email.com";
	private static final String reqId = UUID.randomUUID().toString();
	private static final String reqEmail = "req@email.com";
	@Mock
	private PortalNotificationRepository mockNotificationRepo;

	@Mock
	private GroupNotificationRequestRepository mockGroupNotificationRequestRepository;

	@Mock
	private RoleRepository mockRoleRepository;

	@Mock
	private NotificationServiceConfig mockNotificationSrvConfig;

	@Mock
	private EmailLogService mockEmailService;

	@Mock
	private AuditService mockAuditService;

	@Mock
	private UserService mockUserService;

	private NotificationServiceImpl classUnderTest;

	@BeforeEach
	public void init() {
		classUnderTest = new NotificationServiceImpl(mockNotificationRepo, mockGroupNotificationRequestRepository,
				mockRoleRepository, mockNotificationSrvConfig, mockEmailService, mockAuditService, mockUserService);
	}

	@Test
	public void testSendGroupNotifications_Success() {
		User requester = createUser(reqId, reqEmail);
		Role roleProvider = new Role();
		roleProvider.setRoleName(PPERole.ROLE_PPE_PARTICIPANT.name());
		Set<Role> roles = new HashSet<>();
		roles.add(roleProvider);

		GroupNotificationRequest notification = new GroupNotificationRequest();
		notification.setRequestId(-1L);
		notification.setMessageEnglish(messageEnglish);
		notification.setMessageSpanish(messageSpanish);
		notification.setSubjectEnglish(subjectEnglish);
		notification.setSubjectSpanish(subjectSpanish);
		notification.setRequester(requester);
		notification.setRecipientRoles(roles);

		User user1 = createUser(userId1, email1);
		user1.setAllowEmailNotification(true);
		User user2 = createUser(userId2, email2);
		user2.setAllowEmailNotification(false);
		List<User> recipientGroups = Arrays.asList(user1, user2);

		try {
			when(mockRoleRepository.findByRoleName(roleProvider.getRoleName())).thenReturn(roleProvider);
			when(mockGroupNotificationRequestRepository.save(any(GroupNotificationRequest.class)))
					.thenReturn(notification);
			when(mockUserService.getUsersByRole(roles)).thenReturn(recipientGroups);
			when(mockNotificationRepo.save(any(PortalNotification.class))).thenAnswer(i -> i.getArguments()[0]);

			classUnderTest.sendGroupNotifications(notification);

			verify(mockRoleRepository).findByRoleName(roleProvider.getRoleName());
			verify(mockGroupNotificationRequestRepository).save(any(GroupNotificationRequest.class));
			verify(mockNotificationRepo, times(recipientGroups.size())).save(any(PortalNotification.class));
			verify(mockEmailService).sendEmailNotification(anyString(), isNull(), anyString(), anyString());

		} catch (JsonProcessingException e) {
			fail(e.getMessage(), e);
		}

	}

	private User createUser(String userUUID, String userEmail) {
		User user = new User();
		user.setUserId(1L);
		user.setUserUUID(userUUID);
		user.setEmail(userEmail);
		return user;
	}
}
