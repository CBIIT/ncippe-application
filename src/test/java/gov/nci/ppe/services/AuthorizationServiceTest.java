package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Mocha;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.impl.AuthorizationServiceImpl;

@ActiveProfiles("unittest")
@Tag("service")
@DisplayName("Unit Tests for AutherizationServiceImpl class")
public class AuthorizationServiceTest {
	private static final String targetUUID = "aaaa-bbbb";
	private static final String requester = "req-user";
	private static final String assigned = "assigned-user";

	@Mock
	protected Logger logger;

	@Mock
	public UserService userService;

	@Mock
	public AuditService mockAuditService;

	@InjectMocks
	private AuthorizationServiceImpl authService;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Nested
	class TestAuthorize {

		@Test
		public void testAuthorize_Target_UUID_Not_Found() {
			when(userService.findByUuid(targetUUID)).thenReturn(Optional.empty());

			boolean result = authService.authorize(requester, targetUUID);

			assertFalse(result);
			verify(userService).findByUuid(targetUUID);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { " ", "", "\t", "\n" })
		public void testAuthorize_Req_UUID_BLANK(String reqInput) {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);

			assertFalse(authService.authorize(reqInput, targetUUID));

		}

		@Test
		public void testAuthorize_Req_User_Self() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);

			assertTrue(authService.authorize(targetUUID, targetUUID));
			verify(userService).findByUuid(targetUUID);
		}

		@Test
		public void testAuthorize_Req_User_Not_Found() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);
			when(userService.findByUuid(requester)).thenReturn(Optional.empty());

			assertFalse(authService.authorize(requester, targetUUID));
			verify(userService).findByUuid(targetUUID);
			verify(userService).findByUuid(requester);
		}

		@Test
		public void testAuthorize_Mocha() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);
			Optional<User> mochaOpt = createRequestingUser(PPERole.ROLE_PPE_MOCHA_ADMIN, requester);
			when(userService.findByUuid(requester)).thenReturn(mochaOpt);

			assertTrue(authService.authorize(requester, targetUUID));
		}

		@Test
		public void testAuthorize_Admin() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);
			Optional<User> adminOpt = createRequestingUser(PPERole.ROLE_PPE_ADMIN, requester);
			when(userService.findByUuid(requester)).thenReturn(adminOpt);

			assertFalse(authService.authorize(requester, targetUUID));

		}

		@Test
		public void testAuthorize_CRC_Success() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);
			Optional<User> crcOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);
			((Participant) optionalUser.get()).setCrc((CRC) crcOpt.get());
			when(userService.findByUuid(requester)).thenReturn(crcOpt);

			assertTrue(authService.authorize(requester, targetUUID));

		}

		@Test
		public void testAuthorize_CRC_Fail() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			CRC assignedCRC = (CRC) createRequestingUser(PPERole.ROLE_PPE_CRC, assigned).get();
			((Participant) optionalUser.get()).setCrc(assignedCRC);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);
			Optional<User> crcOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);

			when(userService.findByUuid(requester)).thenReturn(crcOpt);

			assertFalse(authService.authorize(requester, targetUUID));

		}

		@Test
		public void testAuthorize_Provider_Success() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);
			Optional<User> providerOpt = createRequestingUser(PPERole.ROLE_PPE_PROVIDER, requester);
			Set<Provider> providers = new HashSet<>();
			providers.add((Provider) providerOpt.get());
			((Participant) optionalUser.get()).setProviders(providers);
			when(userService.findByUuid(requester)).thenReturn(providerOpt);

			assertTrue(authService.authorize(requester, targetUUID));

		}

		@Test
		public void testAuthorize_Provider_Fail() {
			Optional<User> optionalUser = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Provider assignedProvider = (Provider) createRequestingUser(PPERole.ROLE_PPE_PROVIDER, assigned).get();
			Set<Provider> providers = new HashSet<>();
			providers.add(assignedProvider);
			((Participant) optionalUser.get()).setProviders(providers);

			when(userService.findByUuid(targetUUID)).thenReturn(optionalUser);

			Optional<User> requestingProvider = createRequestingUser(PPERole.ROLE_PPE_PROVIDER, requester);
			when(userService.findByUuid(requester)).thenReturn(requestingProvider);

			assertFalse(authService.authorize(requester, targetUUID));

		}

	}

	@Nested
	class TestAuthorizeFileUpload {
		@Test
		public void testAuthorizeFileUpload_Requester_Not_Found() {
			when(userService.findByUuid(requester)).thenReturn(Optional.empty());

			assertFalse(authService.authorizeFileUpload(requester, targetUUID,
					FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType()));
			verify(userService).findByUuid(requester);
		}

		@Test
		public void testAuthorizeFileUpload_Patient_Not_Found() {
			when(userService.findByUuid(requester)).thenReturn(createRequestingUser(PPERole.ROLE_PPE_CRC, requester));
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(Optional.empty());

			assertFalse(authService.authorizeFileUpload(requester, targetUUID,
					FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType()));
			verify(userService).findByUuid(requester);
			verify(userService).findActiveParticipantByPatientId(targetUUID);

		}

		@Test
		public void testAuthorizeFileUpload_Invalid_File_Type() {
			when(userService.findByUuid(requester)).thenReturn(createRequestingUser(PPERole.ROLE_PPE_CRC, requester));
			when(userService.findActiveParticipantByPatientId(targetUUID))
					.thenReturn(createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID));

			assertFalse(authService.authorizeFileUpload(requester, targetUUID, "BAD_FILE_TYPE"));
			verify(userService).findByUuid(requester);
			verify(userService).findActiveParticipantByPatientId(targetUUID);

		}

		@Test
		public void testAuthorizeFileUpload_eConsent_Success() {
			Optional<User> requestingCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);
			Optional<User> targetOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Participant target = (Participant) targetOpt.get();
			target.setCrc((CRC) requestingCRCOpt.get());
			when(userService.findByUuid(requester)).thenReturn(requestingCRCOpt);
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(targetOpt);

			assertTrue(authService.authorizeFileUpload(requester, targetUUID,
					FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType()));
			verify(userService).findByUuid(requester);
			verify(userService).findActiveParticipantByPatientId(targetUUID);

		}

		@Test
		public void testAuthorizeFileUpload_eConsent_Wrong_CRC() {
			Optional<User> requestingCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);
			Optional<User> targetOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Optional<User> assignedCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, assigned);

			Participant target = (Participant) targetOpt.get();
			target.setCrc((CRC) assignedCRCOpt.get());
			when(userService.findByUuid(requester)).thenReturn(requestingCRCOpt);
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(targetOpt);

			assertFalse(authService.authorizeFileUpload(requester, targetUUID,
					FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType()));
			verify(userService).findByUuid(requester);
			verify(userService).findActiveParticipantByPatientId(targetUUID);

		}

		@Test
		public void testAuthorizeFileUpload_BioMarker_Success() {
			Optional<User> requestingMochaAdminOpt = createRequestingUser(PPERole.ROLE_PPE_MOCHA_ADMIN, requester);
			Optional<User> targetOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);

			when(userService.findByUuid(requester)).thenReturn(requestingMochaAdminOpt);
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(targetOpt);

			assertTrue(authService.authorizeFileUpload(requester, targetUUID,
					FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType()));
			verify(userService).findByUuid(requester);
			verify(userService).findActiveParticipantByPatientId(targetUUID);

		}

		@Test
		public void testAuthorizeFileUpload_eConsent_Failure_Not_Mocha() {
			Optional<User> requestingCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);
			Optional<User> targetOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Participant target = (Participant) targetOpt.get();
			target.setCrc((CRC) requestingCRCOpt.get());
			when(userService.findByUuid(requester)).thenReturn(requestingCRCOpt);
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(targetOpt);

			assertFalse(authService.authorizeFileUpload(requester, targetUUID,
					FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType()));
			verify(userService).findByUuid(requester);
			verify(userService).findActiveParticipantByPatientId(targetUUID);

		}

	}

	@Nested
	class TestAuthorizeFileDownload {

		@ParameterizedTest
		@EnumSource(FileType.class)
		public void testAuthorizeFileDownload_Invalid_Requester(FileType fileType) {
			when(userService.findByUuid(requester)).thenReturn(Optional.empty());

			assertFalse(authService.authorizeFileDownload(requester, targetUUID, fileType.getFileType()));
			verify(userService).findByUuid(requester);
		}

		@ParameterizedTest
		@EnumSource(FileType.class)
		public void testAuthorizeFileDownload_Error_Patient_Not_Found(FileType fileType) {
			Optional<User> crcOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);
			when(userService.findByUuid(requester)).thenReturn(crcOpt);
			when(userService.findActiveParticipantByPatientId(requester)).thenReturn(Optional.empty());

			assertFalse(authService.authorizeFileDownload(requester, requester, fileType.getFileType()));

		}

		@ParameterizedTest
		@EnumSource(FileType.class)
		public void testAuthorizeFileDownload_Success_Self(FileType fileType) {
			Optional<User> patientOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, requester);
			when(userService.findByUuid(requester)).thenReturn(patientOpt);
			when(userService.findActiveParticipantByPatientId(requester)).thenReturn(patientOpt);

			assertTrue(authService.authorizeFileDownload(requester, requester, fileType.getFileType()));

		}

		@ParameterizedTest
		@EnumSource(FileType.class)
		public void testAuthorizedFileDownload_Success_CRC(FileType fileType) {
			Optional<User> patientOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Optional<User> requestingCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, requester);

			Participant patient = (Participant) patientOpt.get();
			patient.setCrc((CRC) requestingCRCOpt.get());

			when(userService.findByUuid(requester)).thenReturn(requestingCRCOpt);
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(patientOpt);

			assertTrue(authService.authorizeFileDownload(requester, targetUUID, fileType.getFileType()));

		}

		@ParameterizedTest
		@EnumSource(FileType.class)
		public void testAuthorizedFileDownload_Success_Provider(FileType fileType) {
			Optional<User> patientOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Optional<User> requestingProviderOpt = createRequestingUser(PPERole.ROLE_PPE_PROVIDER, requester);
			Set<Provider> providers = new HashSet<Provider>();
			providers.add((Provider) requestingProviderOpt.get());
			Optional<User> assignedCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, assigned);

			Participant patient = (Participant) patientOpt.get();
			patient.setCrc((CRC) assignedCRCOpt.get());
			patient.setProviders(providers);

			when(userService.findByUuid(requester)).thenReturn(requestingProviderOpt);
			when(userService.findByUuid(assigned)).thenReturn(assignedCRCOpt);

			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(patientOpt);

			assertTrue(authService.authorizeFileDownload(requester, targetUUID, fileType.getFileType()));

		}

		@ParameterizedTest
		@EnumSource(FileType.class)
		public void testAuthorizedFileDownload_Failure_Unassigned_Provider(FileType fileType) {
			Optional<User> patientOpt = createRequestingUser(PPERole.ROLE_PPE_PARTICIPANT, targetUUID);
			Optional<User> requestingProviderOpt = createRequestingUser(PPERole.ROLE_PPE_PROVIDER, requester);
			final String assigned_prov = assigned + "-prov";
			Optional<User> assignedProviderOpt = createRequestingUser(PPERole.ROLE_PPE_PROVIDER, assigned_prov);
			Set<Provider> providers = new HashSet<Provider>();
			providers.add((Provider) assignedProviderOpt.get());
			Optional<User> assignedCRCOpt = createRequestingUser(PPERole.ROLE_PPE_CRC, assigned);

			Participant patient = (Participant) patientOpt.get();
			patient.setCrc((CRC) assignedCRCOpt.get());
			patient.setProviders(providers);

			when(userService.findByUuid(requester)).thenReturn(requestingProviderOpt);
			when(userService.findByUuid(assigned)).thenReturn(assignedCRCOpt);
			when(userService.findByUuid(assigned_prov)).thenReturn(assignedProviderOpt);
			when(userService.findActiveParticipantByPatientId(targetUUID)).thenReturn(patientOpt);

			assertFalse(authService.authorizeFileDownload(requester, targetUUID, fileType.getFileType()));

		}
	}

	private Optional<User> createRequestingUser(PPERole userRole, String uuid) {
		User user;
		switch (userRole) {
		case ROLE_PPE_CRC:
			user = new CRC();
			break;
		case ROLE_PPE_PARTICIPANT:
			user = new Participant();
			break;
		case ROLE_PPE_MOCHA_ADMIN:
			user = new Mocha();
			break;
		case ROLE_PPE_PROVIDER:
			user = new Provider();
			break;
		default:
			user = new User();
		}
		user.setUserUUID(uuid);
		Role role = new Role();
		role.setRoleName(userRole.name());
		user.setRole(role);
		return Optional.of(user);
	}

}
