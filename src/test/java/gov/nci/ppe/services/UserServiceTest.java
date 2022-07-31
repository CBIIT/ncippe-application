package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.constants.DatabaseConstants.PortalAccountStatus;
import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.CRCRepository;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.data.repository.ParticipantRepository;
import gov.nci.ppe.data.repository.ProviderRepository;
import gov.nci.ppe.data.repository.QuestionAnswerRepository;
import gov.nci.ppe.data.repository.RoleRepository;
import gov.nci.ppe.data.repository.UserRepository;
import gov.nci.ppe.exception.BusinessConstraintViolationException;
import gov.nci.ppe.services.impl.UserServiceImpl;

@ActiveProfiles("unittest")
@DisplayName("Unit Tests for UserServiceImpl class")
@Tag("service")
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private Logger logger;

	private static final String PARTICIPANT_UUID = "part-bbbb-cccc-dddd";
	private static final String PROVIDER_UUID = "prov-bbbb-cccc-dddd";
	private static final String CRC_UUID = "crcc-bbbb-cccc-dddd";
	private static final String PART_EMAIL = "patient@example.com";

	private static final String phoneNumber = null;

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

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetAllRegisteredUsers() {
		List<User> responseList = new ArrayList<>();
		Participant pa = new Participant();
		responseList.add(pa);

		when(userRepository.findAll()).thenReturn(responseList);

		List<User> result = userService.getAllRegisteredUsers();
		verify(userRepository, times(1)).findAll();
		assertNotNull(result);
	}

	@Nested
	class Test_FindByUuid {
		@Test
		public void testFindByUuid_Participant() {
			Participant pa = new Participant();
			pa.setUserUUID(PARTICIPANT_UUID);
			List<PortalNotification> notifications = new ArrayList<>();
			PortalNotification notice = new PortalNotification();
			notifications.add(notice);
			pa.setNotifications(notifications);

			Optional<User> userOpt = Optional.of(pa);

			when(userRepository.findByUserUUID(PARTICIPANT_UUID)).thenReturn(userOpt);

			Optional<User> resultOpt = userService.findByUuid(PARTICIPANT_UUID);

			verify(userRepository).findByUserUUID(PARTICIPANT_UUID);
			assertTrue(resultOpt.isPresent());
			Participant result = (Participant) resultOpt.get();
			assertFalse(result.getNotifications().isEmpty());
			assertFalse(result.isHasNewReports());
		}

		@Test
		public void testFindByUuids_Provider() {
			Participant pa = new Participant();
			pa.setUserUUID(PARTICIPANT_UUID);

			List<PortalNotification> notifications = new ArrayList<>();
			PortalNotification notice = new PortalNotification();
			notifications.add(notice);
			pa.setNotifications(notifications);

			FileMetadata report = new FileMetadata();
			report.setFileType(getFileTypeCode(FileType.PPE_FILETYPE_BIOMARKER_REPORT));
			List<FileMetadata> reports = new ArrayList<>();
			reports.add(report);
			pa.setReports(reports);

			Provider prov = new Provider();
			prov.setUserUUID(PROVIDER_UUID);
			prov.setRole(getRole(PPERole.ROLE_PPE_PROVIDER));

			Set<Participant> patients = new HashSet<>();
			patients.add(pa);
			prov.setPatients(patients);

			Optional<User> provOpt = Optional.of(prov);
			when(userRepository.findByUserUUID(PROVIDER_UUID)).thenReturn(provOpt);

			Optional<User> resultOpt = userService.findByUuid(PROVIDER_UUID);

			verify(userRepository).findByUserUUID(PROVIDER_UUID);
			assertTrue(resultOpt.isPresent());
			Provider provResult = (Provider) resultOpt.get();
			assertFalse(provResult.getPatients().isEmpty());
			provResult.getPatients().stream().forEach(pat -> assertTrue(pat.getNotifications().isEmpty()));
		}
	}

	@Nested
	class TestUpdateUserDetails {
		@Test
		public void testUpdateUserDetails_UserNotFound() {
			when(userRepository.findByUserUUID(PARTICIPANT_UUID)).thenReturn(Optional.empty());
			when(userRepository.findByUserUUID(CRC_UUID)).thenReturn(Optional.empty());
			Optional<User> resultOpt = userService.updateUserDetails(PARTICIPANT_UUID, true, phoneNumber,
					LanguageOption.ENGLISH, CRC_UUID);

			verify(userRepository).findByUserUUID(PARTICIPANT_UUID);
			verify(userRepository).findByUserUUID(CRC_UUID);
			assertTrue(resultOpt.isEmpty());
		}

		@Test
		public void testUpdateUserDetails_Success() {
			Participant pa = new Participant();
			pa.setUserUUID(PARTICIPANT_UUID);
			Optional<User> participantOpt = Optional.of(pa);
			CRC crc = new CRC();
			crc.setUserId(-1L);
			Optional<User> crcOpt = Optional.of(crc);

			when(userRepository.findByUserUUID(PARTICIPANT_UUID)).thenReturn(participantOpt);
			when(userRepository.findByUserUUID(CRC_UUID)).thenReturn(crcOpt);
			when(userRepository.save(pa)).thenReturn(pa);

			Optional<User> resultOpt = userService.updateUserDetails(PARTICIPANT_UUID, true, phoneNumber,
					LanguageOption.ENGLISH, CRC_UUID);

			verify(userRepository).findByUserUUID(PARTICIPANT_UUID);
			verify(userRepository).findByUserUUID(CRC_UUID);
			verify(userRepository).save(pa);

			assertTrue(resultOpt.isPresent());
			Participant result = (Participant) resultOpt.get();
			assertAll(() -> assertEquals(crc.getUserId(), result.getLastRevisedUser()),
					() -> assertEquals(phoneNumber, result.getPhoneNumber()),
					() -> assertEquals(LanguageOption.ENGLISH, result.getPreferredLanguage()),
					() -> assertTrue(result.isAllowEmailNotification()));

		}
	}

	@Nested
	class TestActivateUser {
		@Test
		public void testActivateUser_UserNotFound() {
			when(userRepository.findByEmail(PART_EMAIL)).thenReturn(Optional.empty());
			Optional<User> result = userService.activateUser(PART_EMAIL, CRC_UUID);
			verify(userRepository).findByEmail(PART_EMAIL);
			assertTrue(result.isEmpty());
		}

		@Test
		public void testActivateUser_UserInActive() {
			Participant pa = new Participant();
			pa.setUserUUID(PARTICIPANT_UUID);
			Code status = new Code();
			status.setCodeName(PortalAccountStatus.ACCT_TERMINATED_AT_PPE.name());
			pa.setPortalAccountStatus(status);

			when(userRepository.findByEmail(PART_EMAIL)).thenReturn(Optional.of(pa));

			Optional<User> result = userService.activateUser(PART_EMAIL, CRC_UUID);
			assertTrue(result.isEmpty());
			verify(userRepository).findByEmail(PART_EMAIL);
		}

		@Test
		public void testActivateUser_Success() {

			Participant pa = new Participant();
			pa.setUserId(-1L);
			when(userRepository.findByEmail(PART_EMAIL)).thenReturn(Optional.of(pa));
			when(userRepository.save(pa)).then(returnsFirstArg());
			when(codeRepository.findByCodeName(PortalAccountStatus.ACCT_ACTIVE.name()))
					.thenReturn(getCode(PortalAccountStatus.ACCT_ACTIVE.name()));
			Optional<User> resultOpt = userService.activateUser(PART_EMAIL, PARTICIPANT_UUID);
			verify(userRepository).findByEmail(PART_EMAIL);
			verify(userRepository).save(pa);
			assertTrue(resultOpt.isPresent());
			Participant result = (Participant) resultOpt.get();
			assertAll(() -> assertEquals(PARTICIPANT_UUID, result.getUserUUID()),
					() -> assertEquals(pa.getUserId(), result.getLastRevisedUser()),
					() -> assertEquals(result.getLastRevisedDate(), result.getDateActivated()),
					() -> assertEquals(PortalAccountStatus.ACCT_ACTIVE.name(),
							result.getPortalAccountStatus().getCodeName()));

		}
	}

	private Role getRole(PPERole roleType) {
		Role role = new Role();
		role.setRoleName(roleType.name());
		return role;
	}

	private Code getFileTypeCode(FileType fileType) {
		return getCode(fileType.getFileType());
	}

	private Code getCode(String codeName) {
		Code code = new Code();
		code.setCodeName(codeName);
		return code;
	}

	@Test
	public void testGenerateUnreadReportReminderNotification() {
		int daysUnread = 7;

		LocalDate today = LocalDate.now();
		LocalDateTime startOfPeriod = today.minusDays(daysUnread).atStartOfDay();
		LocalDateTime endOfPeriod = startOfPeriod.plusDays(1);

		Code biomarkerReportType = new Code();
		biomarkerReportType.setCodeId(-1l);
		final String codeName = FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType();
		biomarkerReportType.setCodeName(codeName);

		List<FileMetadata> files = new ArrayList<>();
		when(codeRepository.findByCodeName(codeName)).thenReturn(biomarkerReportType);
		when(fileService.getFilesUploadedBetween(biomarkerReportType, startOfPeriod, endOfPeriod)).thenReturn(files);

		userService.generateUnreadReportReminderNotification(daysUnread);

		verify(fileService).getFilesUploadedBetween(biomarkerReportType, startOfPeriod, endOfPeriod);
		verify(codeRepository).findByCodeName(codeName);
	}

	@Test
	public void testUpdateEmail_FailUUIDPresent() {
		String patientId = "PATID001";
		String newEmail = "newEmail@example.com";
		Participant userToUpdate = new Participant();
		userToUpdate.setUserUUID(UUID.randomUUID().toString());
		when(participantRepository.findByPatientId(patientId)).thenReturn(Optional.of(userToUpdate));
		assertThrows(BusinessConstraintViolationException.class,
				() -> userService.updatePatientEmail(patientId, newEmail, UUID.randomUUID().toString()));
	}

	@Test
	public void testUpdateEmail_Success() {
		String patientId = "PATID001";
		String newEmail = "newEmail@example.com";
		Participant userToUpdate = new Participant();
		userToUpdate.setPatientId(patientId);
		userToUpdate.setUserUUID(null);
		when(participantRepository.findByPatientId(patientId)).thenReturn(Optional.of(userToUpdate));
		when(participantRepository.save(userToUpdate)).thenReturn(userToUpdate);
		try {
			User updatedUser = userService.updatePatientEmail(patientId, newEmail, UUID.randomUUID().toString()).get();

			assertEquals(updatedUser.getEmail(), userToUpdate.getEmail());
		} catch (BusinessConstraintViolationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSynchronizeUserEmailWithLogin_replace_old_email() {
		String oldEmail = "oldemail@example.com";
		String newEmail = "newEmail@example.com";
		String userUuid = UUID.randomUUID().toString();
		User user = new User();
		user.setUserUUID(userUuid);
		user.setEmail(oldEmail);
		when(userRepository.save(user)).then(AdditionalAnswers.returnsFirstArg());
		User updatedUser = userService.synchronizeUserEmailWithLogin(user, userUuid, newEmail).get();

		assertNotNull(updatedUser);
		assertEquals(updatedUser.getEmail(), newEmail);
	}

	@Test
	public void testSynchronizeUserEmailWithLogin_matching_email() {
		String oldEmail = "oldemail@example.com";

		String userUuid = UUID.randomUUID().toString();
		User user = new User();
		user.setUserUUID(userUuid);
		user.setEmail(oldEmail);

		User updatedUser = userService.synchronizeUserEmailWithLogin(user, userUuid, oldEmail).get();

		assertNotNull(updatedUser);
		assertEquals(updatedUser.getEmail(), oldEmail);
		verifyNoMoreInteractions(userRepository);
	}

}
