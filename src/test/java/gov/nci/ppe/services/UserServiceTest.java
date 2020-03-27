package gov.nci.ppe.services;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.repository.CRCRepository;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.data.repository.ParticipantRepository;
import gov.nci.ppe.data.repository.ProviderRepository;
import gov.nci.ppe.data.repository.QuestionAnswerRepository;
import gov.nci.ppe.data.repository.RoleRepository;
import gov.nci.ppe.data.repository.UserRepository;
import gov.nci.ppe.services.impl.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CodeRepository codeRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private ParticipantRepository participantRepository;

	@Mock
	private QuestionAnswerRepository qsAnsRepo;

	@Mock
	private ProviderRepository providerRepository;

	@Mock
	private CRCRepository crcRepository;

	@Mock
	private EmailLogService emailService;

	@Mock
	private NotificationServiceConfig notificationServiceConfig;

	@Mock
	private NotificationService notificationService;

	@Mock
	private FileService fileService;

	@Mock
	private AuditService auditService;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGenerateUnreadReportReminderNotification() {
		int daysUnread = 7;

		LocalDate today = LocalDate.now();
		LocalDateTime startOfPeriod = today.minusDays(daysUnread).atStartOfDay();
		LocalDateTime endOfPeriod = startOfPeriod.plusDays(1);

		List<FileMetadata> files = new ArrayList<>();
		when(fileService.getFilesUploadedBetween(startOfPeriod, endOfPeriod)).thenReturn(files);

		userService.generateUnreadReportReminderNotification(daysUnread);
	}

}
