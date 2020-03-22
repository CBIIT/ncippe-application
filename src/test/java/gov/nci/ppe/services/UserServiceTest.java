package gov.nci.ppe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
import gov.nci.ppe.services.impl.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private Logger logger;

	@Mock
	private UserRepository mockUserRepo;

	@Mock
	private CodeRepository mockCodeRepo;

	@Mock
	private RoleRepository mockRoleRepo;

	@Mock
	private ParticipantRepository mockParticipantRepo;

	@Mock
	private QuestionAnswerRepository mockQsAnsRepo;

	@Mock
	private ProviderRepository mockProviderRepo;

	@Mock
	private CRCRepository mockCrcRepo;

	private static final String PARTICIPANT_UUID = "part-bbbb-cccc-dddd";
	private static final String PROVIDER_UUID = "prov-bbbb-cccc-dddd";
	private static final String CRC_UUID = "crcc-bbbb-cccc-dddd";
	private static final String PART_EMAIL = "patient@example.com";

	private static final String phoneNumber = null;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetAllRegisteredUsers() {
		List<User> responseList = new ArrayList<>();
		Participant pa = new Participant();
		responseList.add(pa);

		when(mockUserRepo.findAll()).thenReturn(responseList);

		List<User> result = userService.getAllRegisteredUsers();
		verify(mockUserRepo, times(1)).findAll();
		assertNotNull(result);
	}

	@Test
	public void testFindByUuid_Participant() {
		Participant pa = new Participant();
		pa.setUserUUID(PARTICIPANT_UUID);
		List<PortalNotification> notifications = new ArrayList<>();
		PortalNotification notice = new PortalNotification();
		notifications.add(notice);
		pa.setNotifications(notifications);

		Optional<User> userOpt = Optional.of(pa);

		when(mockUserRepo.findByUserUUID(PARTICIPANT_UUID)).thenReturn(userOpt);

		Optional<User> resultOpt = userService.findByUuid(PARTICIPANT_UUID);

		verify(mockUserRepo).findByUserUUID(PARTICIPANT_UUID);
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
		when(mockUserRepo.findByUserUUID(PROVIDER_UUID)).thenReturn(provOpt);

		Optional<User> resultOpt = userService.findByUuid(PROVIDER_UUID);

		verify(mockUserRepo).findByUserUUID(PROVIDER_UUID);
		assertTrue(resultOpt.isPresent());
		Provider provResult = (Provider) resultOpt.get();
		assertFalse(provResult.getPatients().isEmpty());
		provResult.getPatients().stream().forEach(pat -> assertTrue(pat.getNotifications().isEmpty()));
	}

	@Test
	public void testUpdateUserDetails_UserNotFound() {
		when(mockUserRepo.findByUserUUID(PARTICIPANT_UUID)).thenReturn(Optional.empty());
		when(mockUserRepo.findByUserUUID(CRC_UUID)).thenReturn(Optional.empty());
		Optional<User> resultOpt = userService.updateUserDetails(PARTICIPANT_UUID, true, phoneNumber,
				LanguageOption.ENGLISH, CRC_UUID);

		verify(mockUserRepo).findByUserUUID(PARTICIPANT_UUID);
		verify(mockUserRepo).findByUserUUID(CRC_UUID);
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

		when(mockUserRepo.findByUserUUID(PARTICIPANT_UUID)).thenReturn(participantOpt);
		when(mockUserRepo.findByUserUUID(CRC_UUID)).thenReturn(crcOpt);
		when(mockUserRepo.save(pa)).thenReturn(pa);

		Optional<User> resultOpt = userService.updateUserDetails(PARTICIPANT_UUID, true, phoneNumber,
				LanguageOption.ENGLISH, CRC_UUID);

		verify(mockUserRepo).findByUserUUID(PARTICIPANT_UUID);
		verify(mockUserRepo).findByUserUUID(CRC_UUID);
		verify(mockUserRepo).save(pa);

		assertTrue(resultOpt.isPresent());
		Participant result = (Participant) resultOpt.get();
		assertEquals(crc.getUserId(), result.getLastRevisedUser());
		assertEquals(phoneNumber, result.getPhoneNumber());
		assertEquals(LanguageOption.ENGLISH, result.getPreferredLanguage());
		assertTrue(result.isAllowEmailNotification());

	}

	@Test
	public void testActivateUser_UserNotFound() {
		when(mockUserRepo.findByEmail(PART_EMAIL)).thenReturn(Optional.empty());
		Optional<User> result = userService.activateUser(PART_EMAIL, CRC_UUID);
		verify(mockUserRepo).findByEmail(PART_EMAIL);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testActivateUser_UserInActive() {
		Participant pa = new Participant();
		pa.setUserUUID(PARTICIPANT_UUID);
		Code status = new Code();
		status.setCodeName(PortalAccountStatus.ACCT_TERMINATED_AT_PPE.name());
		pa.setPortalAccountStatus(status);

		when(mockUserRepo.findByEmail(PART_EMAIL)).thenReturn(Optional.of(pa));

		Optional<User> result = userService.activateUser(PART_EMAIL, CRC_UUID);
		assertTrue(result.isEmpty());
		verify(mockUserRepo).findByEmail(PART_EMAIL);
	}

	@Test
	public void testActivateUser_Success() {

		Participant pa = new Participant();
		pa.setUserId(-1L);
		when(mockUserRepo.findByEmail(PART_EMAIL)).thenReturn(Optional.of(pa));
		when(mockUserRepo.save(pa)).then(returnsFirstArg());
		when(mockCodeRepo.findByCodeName(PortalAccountStatus.ACCT_ACTIVE.name()))
				.thenReturn(getCode(PortalAccountStatus.ACCT_ACTIVE.name()));
		Optional<User> resultOpt = userService.activateUser(PART_EMAIL, PARTICIPANT_UUID);
		verify(mockUserRepo).findByEmail(PART_EMAIL);
		verify(mockUserRepo).save(pa);
		assertTrue(resultOpt.isPresent());
		Participant result = (Participant) resultOpt.get();
		assertEquals(PARTICIPANT_UUID, result.getUserUUID());
		assertEquals(pa.getUserId(), result.getLastRevisedUser());
		assertEquals(result.getLastRevisedDate(), result.getDateActivated());
		assertEquals(PortalAccountStatus.ACCT_ACTIVE.name(), result.getPortalAccountStatus().getCodeName());

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

}
