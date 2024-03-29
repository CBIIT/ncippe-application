package gov.nci.ppe.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.configurations.NotificationServiceConfig;
import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.constants.CommonConstants.LanguageOption;
import gov.nci.ppe.constants.DatabaseConstants.PortalAccountStatus;
import gov.nci.ppe.constants.DatabaseConstants.UserType;
import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.CRC;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.QuestionAnswer;
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
import gov.nci.ppe.open.data.entity.dto.OpenResponseDTO;
import gov.nci.ppe.open.data.entity.dto.UserEnrollmentDataDTO;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.EmailLogService;
import gov.nci.ppe.services.FileService;
import gov.nci.ppe.services.NotificationService;
import gov.nci.ppe.services.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a Service class that orchestrates all the calls to entities and
 * returns JSON responses.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private static final String NEW_PROVIDERS = "NewProviders";

	private UserRepository userRepository;

	private CodeRepository codeRepository;

	private RoleRepository roleRepository;

	private ParticipantRepository participantRepository;

	private QuestionAnswerRepository qsAnsRepo;

	private ProviderRepository providerRepository;

	private CRCRepository crcRepository;

	private EmailLogService emailService;

	private NotificationServiceConfig notificationServiceConfig;

	private NotificationService notificationService;

	private FileService fileService;

	private AuditService auditService;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public UserServiceImpl(UserRepository userRepo, CodeRepository codeRepo, ParticipantRepository participantRepo,
			QuestionAnswerRepository qsAnsRepo, RoleRepository roleRepository, ProviderRepository providerRepository,
			CRCRepository crcRepository, EmailLogService emailService,
			NotificationServiceConfig notificationServiceConfig, NotificationService notificationService,
			FileService fileService, AuditService auditService) {
		super();
		this.userRepository = userRepo;
		this.codeRepository = codeRepo;
		this.participantRepository = participantRepo;
		this.qsAnsRepo = qsAnsRepo;
		this.roleRepository = roleRepository;
		this.providerRepository = providerRepository;
		this.crcRepository = crcRepository;
		this.emailService = emailService;
		this.notificationServiceConfig = notificationServiceConfig;
		this.notificationService = notificationService;
		this.fileService = fileService;
		this.auditService = auditService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> getAllRegisteredUsers() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findByUuid(String userGuid) {
		Optional<User> optionalUser = userRepository.findByUserUUID(userGuid);
		return updateAssociatedPatientRecords(optionalUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> updateUserDetails(String userGuid, Boolean userEmailNotification, String phoneNumber,
			LanguageOption preferredLang, String requestingUserUUID) {
		Optional<User> userOptional = userRepository.findByUserUUID(userGuid);
		Optional<User> requesterOptional = userRepository.findByUserUUID(requestingUserUUID);
		/* Check if the user is present in the system */
		if (userOptional.isEmpty() || requesterOptional.isEmpty()) {
			return userOptional;
		}

		User user = userOptional.get();
		user.setAllowEmailNotification(userEmailNotification);
		user.setPhoneNumber(phoneNumber);
		if (preferredLang != null) {
			user.setPreferredLanguage(preferredLang);
		}
		user.setLastRevisedUser(requesterOptional.get().getUserId());
		user.setLastRevisedDate(LocalDateTime.now());
		User updateUser = userRepository.save(user);

		return Optional.of(updateUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findUserById(Long userId) {
		return userRepository.findById(userId);
	}

	/**
	 * This method clears the patient notifications and marks if the patient has any
	 * reports not viewed by the CRC/Provider
	 * 
	 * @param associatedPatients
	 * @param viewingUser
	 * @return
	 */
	private Set<Participant> updateAssociatedPatientRecordsForCRCandProvider(Set<Participant> associatedPatients,
			User viewingUser) {
		associatedPatients.forEach(patient -> {
			patient.getNotifications().clear();
			patient.getReports().forEach(report -> {
				if (report.getFileType().getCodeName()
						.equalsIgnoreCase(FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType())
						&& !report.getViewedBy().contains(viewingUser)) {
					patient.setHasNewReports(true);
				}
			});
		});
		return associatedPatients;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> activateUser(String userEmail, String userUUID) {
		Optional<User> optionalUser = userRepository.findByEmail(userEmail);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (StringUtils.isBlank(user.getUserUUID())) {
				// Fill in the user's UUID and save.
				user.setUserUUID(userUUID);
				LocalDateTime currentTime = LocalDateTime.now();
				user.setDateActivated(currentTime);
				user.setLastRevisedDate(currentTime);
				user.setLastRevisedUser(user.getUserId());
				user.setPortalAccountStatus(codeRepository.findByCodeName(PortalAccountStatus.ACCT_ACTIVE.name()));
				user = userRepository.save(user);
				optionalUser = Optional.of(user);
			} else if (user.getPortalAccountStatus().getCodeName()
					.equals(PortalAccountStatus.ACCT_TERMINATED_AT_PPE.name())) {
				optionalUser = Optional.empty();
			}
		}
		return optionalUser;
	}

	@Override
	public Optional<User> findByUuidAndPortalAccountStatus(String userGuid, List<String> accountStatusList) {
		List<Code> accountStatusCodeList = convertToCode(accountStatusList);
		Optional<User> optionalUser = userRepository.findByUserUUIDAndPortalAccountStatusIn(userGuid,
				accountStatusCodeList);
		return updateAssociatedPatientRecords(optionalUser);
	}

	private Optional<User> updateAssociatedPatientRecords(Optional<User> optionalUser) {
		if (optionalUser.isPresent()) {
			// For Providers and CRC admins, check if each associated participant has
			// reports that the CRC/Provider has not seen.
			Set<Participant> associatedPatients = null;
			User user = optionalUser.get();
			if (null != user.getRole()) {
				if (user.getRole().getRoleName().equalsIgnoreCase(PPERole.ROLE_PPE_CRC.name())) {
					associatedPatients = ((CRC) user).getPatients();

				} else if (user.getRole().getRoleName().equalsIgnoreCase(PPERole.ROLE_PPE_PROVIDER.name())) {
					associatedPatients = ((Provider) user).getPatients();
				}
			}
			if (associatedPatients != null && !associatedPatients.isEmpty()) {
				updateAssociatedPatientRecordsForCRCandProvider(associatedPatients, user);
			}
		}
		return optionalUser;
	}

	private List<Code> convertToCode(List<String> codeNameList) {
		List<Code> codeList = codeRepository.findByCodeNameIn(codeNameList);
		List<Long> accountStatusCodeIdList = new ArrayList<>();
		codeList.forEach(code -> accountStatusCodeIdList.add(code.getCodeId()));
		return codeList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> deactivateUserPortalAccountStatus(String userUUID) {
		Optional<User> userOptional = userRepository.findByUserUUID(userUUID);
		if (!userOptional.isPresent()) {
			return userOptional;
		}
		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_TERMINATED_AT_PPE.name());
		User user = userOptional.get();
		user.setPortalAccountStatus(portalAccountStatusCode);
		user.setLastRevisedUser(user.getUserId());
		user.setAllowEmailNotification(false);
		user.setLastRevisedDate(LocalDateTime.now());
		User updateUser = userRepository.save(user);
		return Optional.of(updateUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> withdrawParticipationFromBiobankProgram(Participant patient, List<QuestionAnswer> qsAnsList) {
		LocalDateTime qsAnsInsertionTime = LocalDateTime.now();
		qsAnsList.forEach(qs -> {
			qs.setDateAnswered(qsAnsInsertionTime);
		});
		qsAnsRepo.saveAll(qsAnsList);
		patient.setLastRevisedDate(qsAnsInsertionTime);
		patient.setActiveBiobankParticipant(false);
		patient.setDateDeactivated(qsAnsInsertionTime);
		return Optional.of(userRepository.save(patient));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findByEmailAndPortalAccountStatus(String email, List<String> validAccountStatusList) {
		List<Code> accountStatusCodeList = convertToCode(validAccountStatusList);
		Optional<User> optionalUser = userRepository.findByEmailAndPortalAccountStatusIn(email, accountStatusCodeList);
		return updateAssociatedPatientRecords(optionalUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findByPatientIdAndPortalAccountStatus(String patientId, List<String> validAccountStatusList) {
		List<Code> accountStatusCodeList = convertToCode(validAccountStatusList);
		Optional<User> optionalUser = participantRepository.findByPatientIdAndPortalAccountStatusIn(patientId,
				accountStatusCodeList);
		return optionalUser;
	}

	@Override
	public Optional<User> findActiveParticipantByPatientId(String patientId) {
		return participantRepository.findByPatientIdAndIsActiveBiobankParticipantTrue(patientId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> withdrawParticipationFromBiobankProgramAndSendNotification(Participant patient,
			List<QuestionAnswer> qsAnsList) {
		Optional<User> userOptional = withdrawParticipationFromBiobankProgram(patient, qsAnsList);
		Participant withdrawnPatient = (Participant) userOptional.get();
		StringBuilder questionAnswers = new StringBuilder();
		withdrawnPatient.getQuestionAnswers().forEach(qs -> {
			questionAnswers.append("\u2022").append(" ").append(qs.getQuestion()).append(" : ")
					.append(qs.getAnswer() == null ? "No response provided" : qs.getAnswer()).append("<br/>");
		});
		if (patient.getUserId() == patient.getLastRevisedUser()) {
			if (withdrawnPatient.getCrc().isAllowEmailNotification()) {

				emailService.sendEmailToCRCAfterParticipantWithdraws(withdrawnPatient.getFirstName(),
						withdrawnPatient.getLastName(), withdrawnPatient.getCrc().getFirstName(),
						withdrawnPatient.getCrc().getEmail(), questionAnswers.toString(),
						withdrawnPatient.getPatientId(), withdrawnPatient.getCrc().getPreferredLanguage());
			}
			if (PortalAccountStatus.ACCT_ACTIVE.name()
					.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())
					|| PortalAccountStatus.ACCT_INITIATED.name()
							.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())) {
				String notificationTitleEnglish = notificationServiceConfig.getParticipantWithdrawsSelfSubjectEnglish();
				notificationTitleEnglish = StringUtils.replace(notificationTitleEnglish, "%{FullName}",
						withdrawnPatient.getFullName());
				String notificationTitleSpanish = notificationServiceConfig.getParticipantWithdrawsSelfSubjectSpanish();
				notificationTitleSpanish = StringUtils.replace(notificationTitleSpanish, "%{FullName}",
						withdrawnPatient.getFullName());
				notificationService.addNotification(notificationServiceConfig.getParticipantWithdrawsSelfFrom(),
						notificationTitleEnglish, notificationTitleSpanish,
						notificationServiceConfig.getParticipantWithdrawsSelfMessageEnglish(),
						notificationServiceConfig.getParticipantWithdrawsSelfMessageSpanish(),
						withdrawnPatient.getCrc().getUserId(), withdrawnPatient.getFirstName(),
						withdrawnPatient.getFirstName(), withdrawnPatient.getPatientId());

			}
		} else {
			if (withdrawnPatient.isAllowEmailNotification()) {
				emailService.sendEmailToPatientAfterCRCWithdrawsPatient(withdrawnPatient.getCrc().getFirstName(),
						withdrawnPatient.getCrc().getLastName(), withdrawnPatient.getFirstName(),
						withdrawnPatient.getEmail(), questionAnswers.toString(),
						withdrawnPatient.getPreferredLanguage());
			}
			if (PortalAccountStatus.ACCT_ACTIVE.name()
					.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())
					|| PortalAccountStatus.ACCT_INITIATED.name()
							.equalsIgnoreCase(withdrawnPatient.getPortalAccountStatus().getCodeName())) {

				notificationService.addNotification(notificationServiceConfig.getParticipantWithdrawnByCRCFrom(),
						notificationServiceConfig.getParticipantWithdrawnByCRCSubjectEnglish(),
						notificationServiceConfig.getParticipantWithdrawnByCRCSubjectSpanish(),
						notificationServiceConfig.getParticipantWithdrawnByCRCMessageEnglish(),
						notificationServiceConfig.getParticipantWithdrawnByCRCMessageSpanish(),
						withdrawnPatient.getUserId(), withdrawnPatient.getCrc().getFirstName(),
						withdrawnPatient.getCrc().getLastName(), withdrawnPatient.getPatientId());
			}
		}
		return Optional.of(withdrawnPatient);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> findByEmail(String emailId) {
		return userRepository.findByEmail(emailId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> updateUser(User user) {
		user.setLastRevisedDate(LocalDateTime.now());
		return Optional.of(userRepository.save(user));
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> invitePatientToPortal(String patientId, String uuid) throws JsonProcessingException {
		Optional<User> participantOptional = findActiveParticipantByPatientId(patientId);
		if (participantOptional.isEmpty()) {
			return participantOptional;
		}

		Participant participant = (Participant) participantOptional.get();

		/* Get the UserId for CRC */
		Optional<User> crcOptional = findByUuid(uuid);
		if (crcOptional.isPresent()) {
			participant.setLastRevisedUser(crcOptional.get().getUserId());
			participant.setLastRevisedDate(LocalDateTime.now());
		}

		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_INITIATED.name());
		participant.setPortalAccountStatus(portalAccountStatusCode);

		participantOptional = Optional.of(userRepository.save(participant));

		raiseInvitedParticipationAuditEvent(patientId, uuid, participant.getEmail(), participant.getFirstName(),
				participant.getLastName());

		// Send Notification to Patient & Providers
		emailService.sendEmailToInvitePatient(participant.getEmail(), participant.getFirstName(),
				participant.getPreferredLanguage());
		if (participant.getProviders() != null) {
			for (Provider provider : participant.getProviders()) {
				if (provider.isAllowEmailNotification() && StringUtils.isNotBlank(provider.getEmail())) {
					emailService.sendEmailToProviderOnPatientInvitation(provider.getEmail(), provider.getFirstName(),
							provider.getPreferredLanguage());
				}
				notificationService.notifyProviderWhenPatientIsAdded(participant.getFullName(), provider.getUserId(),
						participant.getPatientId());

			}
		}

		return participantOptional;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> insertNewPatientDetailsFromOpen(Participant newPatient) {
		newPatient.setFirstName(StringUtils.EMPTY);
		newPatient.setLastName(StringUtils.EMPTY);
		Role role = roleRepository.findByRoleName(PPERole.ROLE_PPE_PARTICIPANT.name());
		newPatient.setRole(role);
		Code userType = codeRepository.findByCodeName(UserType.PPE_PARTICIPANT.name());
		newPatient.setUserType(userType);
		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_NEW.name());
		newPatient.setPortalAccountStatus(portalAccountStatusCode);
		newPatient.setAllowEmailNotification(true);
		newPatient.setActiveBiobankParticipant(true);
		newPatient.setDateCreated(LocalDateTime.now());
		newPatient.setLastRevisedDate(LocalDateTime.now());
		Optional<User> patientOptional = Optional.of(userRepository.save(newPatient));
		CRC crc = newPatient.getCrc();
		if (null != crc) {

			// Send System notification to CRC when a new patient is inserted into PPE from
			// OPEN
			notificationService.addNotification(notificationServiceConfig.getPatientAddedFromOpenFrom(),
					notificationServiceConfig.getPatientAddedFromOpenSubjectEnglish(),
					notificationServiceConfig.getPatientAddedFromOpenSubjectSpanish(),
					notificationServiceConfig.getPatientAddedFromOpenMessageEnglish(),
					notificationServiceConfig.getPatientAddedFromOpenMessageSpanish(), crc.getUserId(),
					StringUtils.EMPTY, StringUtils.EMPTY, newPatient.getPatientId());

			// Send Email notification to CRC when a new patient is inserted into PPE from
			// OPEN
			if (crc.isAllowEmailNotification() && StringUtils.isNotBlank(crc.getEmail())) {
				emailService.sendEmailToCRCOnNewPatient(crc.getEmail(), crc.getFirstName(), crc.getPreferredLanguage());
			}

		}
		return patientOptional;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> insertNewProviderDetailsFromOpen(Provider provider) {
		LocalDateTime currentTimestamp = LocalDateTime.now();
		Role role = roleRepository.findByRoleName(PPERole.ROLE_PPE_PROVIDER.name());
		provider.setRole(role);
		Code userType = codeRepository.findByCodeName(UserType.PPE_PROVIDER.name());
		provider.setUserType(userType);
		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_INITIATED.name());
		provider.setPortalAccountStatus(portalAccountStatusCode);
		provider.setAllowEmailNotification(true);
		provider.setDateCreated(currentTimestamp);
		provider.setLastRevisedDate(currentTimestamp);
		Optional<User> providerOptional = Optional.of(userRepository.save(provider));

		// Send Email Notification to Providers
		emailService.sendEmailToInviteNonPatients(provider.getEmail(), provider.getFirstName(),
				provider.getPreferredLanguage());
		return providerOptional;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Provider> findProviderByCtepId(Long ctepId) {
		return providerRepository.findProviderByOpenCtepID(ctepId);
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> insertNewCRCDetailsFromOpen(CRC crc) {
		LocalDateTime currentTimestamp = LocalDateTime.now();
		Role role = roleRepository.findByRoleName(PPERole.ROLE_PPE_CRC.name());
		crc.setRole(role);
		Code userType = codeRepository.findByCodeName(UserType.PPE_CRC.name());
		crc.setUserType(userType);
		Code portalAccountStatusCode = codeRepository.findByCodeName(PortalAccountStatus.ACCT_INITIATED.name());
		crc.setPortalAccountStatus(portalAccountStatusCode);
		crc.setAllowEmailNotification(true);
		crc.setDateCreated(currentTimestamp);
		crc.setLastRevisedDate(currentTimestamp);
		Optional<User> providerOptional = Optional.of(userRepository.save(crc));

		// Send Email Notification to CRCs
		emailService.sendEmailToInviteNonPatients(crc.getEmail(), crc.getFirstName(), crc.getPreferredLanguage());
		return providerOptional;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public List<User> insertDataFetchedFromOpen(OpenResponseDTO openResponseDTO) {

		raiseOpenInsertAuditEvent(openResponseDTO);
		List<String> validAccountStatusList = new ArrayList<>();
		validAccountStatusList.add(PortalAccountStatus.ACCT_NEW.name());
		validAccountStatusList.add(PortalAccountStatus.ACCT_INITIATED.name());
		validAccountStatusList.add(PortalAccountStatus.ACCT_ACTIVE.name());
		validAccountStatusList.add(PortalAccountStatus.ACCT_TERMINATED_AT_LOGIN_GOV.name());
		validAccountStatusList.add(PortalAccountStatus.ACCT_TERMINATED_AT_PPE.name());

		List<UserEnrollmentDataDTO> userEnrollmentData = openResponseDTO.getData();
		List<User> newUsersList = new ArrayList<>();
		userEnrollmentData.forEach(patientData -> {
			Set<Provider> providerSet = new HashSet<>();
			/*
			 * Verify if the provider for a particular patient is already in the system If
			 * not insert the provider details and then associate it with them with the
			 * patient.
			 */
			if (null != patientData.getTreatingInvestigatorCtepId()) {
				Optional<Provider> treatingProviderOptional = findProviderByCtepId(
						patientData.getTreatingInvestigatorCtepId());
				if (treatingProviderOptional.isEmpty()) {
					Provider treatingProvider = generateBasicProviderDetails(
							patientData.getTreatingInvestigatorCtepId(), patientData.getTreatingInvestigatorFirstName(),
							patientData.getTreatingInvestigatorLastName(), patientData.getTreatingInvestigatorPhone(),
							patientData.getTreatingInvestigatorEmail());
					providerSet.add((Provider) insertNewProviderDetailsFromOpen(treatingProvider).get());
					raiseInsertParticipantAuditEvent("ProviderID", Long.toString(treatingProvider.getOpenCtepID()),
							AuditEventType.PPE_INSERT_DATA_FROM_OPEN);
				} else {
					providerSet.add(treatingProviderOptional.get());
				}
			}
			/* A patient can have upto 2 providers simultaneously */
			if (null != patientData.getCreditInvestigatorCtepId()) {
				Optional<Provider> creditProviderOptional = findProviderByCtepId(
						patientData.getCreditInvestigatorCtepId());
				if (creditProviderOptional.isEmpty()) {
					Provider creditProvider = generateBasicProviderDetails(patientData.getCreditInvestigatorCtepId(),
							patientData.getCreditInvestigatorFirstName(), patientData.getCreditInvestigatorLastName(),
							patientData.getCreditInvestigatorPhone(), patientData.getCreditInvestigatorEmail());
					providerSet.add((Provider) insertNewProviderDetailsFromOpen(creditProvider).get());
					raiseInsertParticipantAuditEvent("ProviderID", Long.toString(creditProvider.getOpenCtepID()),
							AuditEventType.PPE_INSERT_DATA_FROM_OPEN);
				} else {
					providerSet.add(creditProviderOptional.get());
				}
			}
			CRC crc = null;
			if (null != patientData.getCraCtepId()) {
				Optional<CRC> crcOptional = findCRCByCtepId(patientData.getCraCtepId());
				if (crcOptional.isEmpty()) {
					crc = new CRC();
					crc.setOpenCtepID(patientData.getCraCtepId());
					crc.setFirstName(patientData.getCraFirstName());
					crc.setLastName(patientData.getCraLastName());
					crc.setPhoneNumber(formatPhoneNumber(patientData.getCraPhone()));
					crc.setEmail(patientData.getCraEmail());
					crc.setPreferredLanguage(LanguageOption.ENGLISH);
					crc = (CRC) insertNewCRCDetailsFromOpen(crc).get();
					raiseInsertParticipantAuditEvent("CRCID", Long.toString(crc.getOpenCtepID()),
							AuditEventType.PPE_INSERT_DATA_FROM_OPEN);
				} else {
					crc = crcOptional.get();
				}
			}
			Optional<User> patientOptional = findByPatientIdAndPortalAccountStatus(patientData.getPatientId(),
					validAccountStatusList);
			if (patientOptional.isEmpty()) {
				Participant newPatient = new Participant();
				newPatient.setPatientId(patientData.getPatientId());
				newPatient.setDateOfBirth(patientData.getDateOfBirth());
				newPatient.setPreferredLanguage(LanguageOption.ENGLISH);
				// Associate the providers & CRC to the patient
				newPatient.setProviders(providerSet);
				if (null != crc) {
					newPatient.setCrc(crc);
				}
				patientOptional = insertNewPatientDetailsFromOpen(newPatient);

				newUsersList.add(patientOptional.get());
				raiseInsertParticipantAuditEvent("PatientID", newPatient.getPatientId(),
						AuditEventType.PPE_INSERT_DATA_FROM_OPEN);
			} else {
				// If the patient exists, check for any changes in the relationship with
				// Providers and CRC
				boolean providerUpdatedFlag = false;
				boolean crcUpdatedFlag = false;
				Participant patient = (Participant) patientOptional.get();
				Set<Provider> existingProviders = patient.getProviders();
				Map<String, Set<Long>> mapOFProviders = new HashMap<>();

				if (!providerSet.isEmpty() || existingProviders.size() != providerSet.size()
						|| !providerSet.containsAll(existingProviders)) {
					patient.setProviders(providerSet);
					mapOFProviders = findDifferenceInProviders(getProviderIds(existingProviders),
							getProviderIds(providerSet));
					providerUpdatedFlag = true;
				}

				CRC existingCRC = patient.getCrc();

				// Adding a new CRC to a patient
				if (null == existingCRC && null != crc) {
					patient.setCrc(crc);
					crcUpdatedFlag = true;
				}
				// Updating CRC for a patient
				if (null != crc && null != existingCRC) {
					// Check if the CRC remains unchanged.
					if (existingCRC.getOpenCtepID() != crc.getOpenCtepID()) {
						patient.setCrc(crc);
						crcUpdatedFlag = true;
					}
				}

				patientOptional = updatePatientDetailsFromOpen(patient);
				newUsersList.add(patientOptional.get());
				if (providerUpdatedFlag) {
					Set<Long> providerOpenId = mapOFProviders.get(NEW_PROVIDERS);
					providerOpenId.forEach(providerCtepId -> {
						Optional<Provider> providerOptional = findProviderByCtepId(providerCtepId);
						if (providerOptional.isPresent()) {
							Provider newProvider = providerOptional.get();
							notificationService.notifyProviderWhenPatientIsAdded(patient.getFullName(),
									newProvider.getUserId(), patient.getPatientId());

							if (newProvider.isAllowEmailNotification()) {
								emailService.sendEmailToProviderOnPatientInvitation(newProvider.getEmail(),
										newProvider.getFirstName(), newProvider.getPreferredLanguage());
							}

						}
					});
					if (patient.isAllowEmailNotification() && StringUtils.isNotBlank(patient.getEmail())) {
						emailService.sendEmailToPatientWhenProviderChanges(patient.getEmail(), patient.getFirstName(),
								patient.getPatientId(), patient.getPreferredLanguage());
					}
					notificationService.notifyPatientWhenProviderIsReplaced(patient.getUserId());
					raiseUpdateParticipantAuditEvent("OldProviderId", "NewProviderId",
							mapOFProviders.get("ExistingProviders"), mapOFProviders.get(NEW_PROVIDERS),
							patient.getPatientId(), AuditEventType.PPE_UPDATE_DATA_FROM_OPEN);
				}
				if (crcUpdatedFlag) {
					if (patient.isAllowEmailNotification() && StringUtils.isNotBlank(patient.getEmail())) {
						emailService.sendEmailToPatientWhenCRCChanges(patient.getEmail(), patient.getFirstName(),
								patient.getPatientId(), patient.getPreferredLanguage());
					}
					// Notify the patient in the system
					notificationService.notifyPatientWhenCRCIsReplaced(patient.getUserId());

					if (null != crc) {
						// Notify the CRC in the system
						notificationService.notifyCRCWhenPatientIsAdded(patient.getFullName(), crc.getUserId(),
								patient.getPatientId());
						if (crc.isAllowEmailNotification()) {
							emailService.sendEmailToCRCWhenPatientIsAdded(crc.getEmail(), crc.getFirstName(),
									crc.getPreferredLanguage());
						}
					}
					final Long crcOpentCtepId = crc.getOpenCtepID();
					if (null != existingCRC) {
						raiseUpdateParticipantAuditEvent("OldCRCId", "NewCRCId", new HashSet<Long>() {
							{
								add(existingCRC.getOpenCtepID());
							}
						}, new HashSet<Long>() {
							{
								add(crcOpentCtepId);
							}
						}, patient.getPatientId(), AuditEventType.PPE_UPDATE_DATA_FROM_OPEN);
					} else {
						raiseUpdateParticipantAuditEvent("OldCRCId", "NewCRCId", new HashSet<Long>(),
								new HashSet<Long>() {
									{
										add(crcOpentCtepId);
									}
								}, patient.getPatientId(), AuditEventType.PPE_UPDATE_DATA_FROM_OPEN);
					}
				}
			}
		});

		return newUsersList;
	}

	private void raiseOpenInsertAuditEvent(OpenResponseDTO openResponseDTO) {
		ObjectNode auditDetail = mapper.createObjectNode();
		auditDetail.set("OpenData", mapper.valueToTree(openResponseDTO));
		try {
			auditService.logAuditEvent(auditDetail, AuditEventType.PPE_INSERT_DATA_FROM_OPEN);
		} catch (JsonProcessingException e) {
			log.error("Error with Auditing OPEN Insert", e);
		}
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CRC> findCRCByCtepId(Long ctepId) {
		return crcRepository.findCRCByOpenCtepID(ctepId);
	}

	private void raiseInvitedParticipationAuditEvent(String patientId, String uuid, String patientEmail,
			String patientFirstName, String patientLastName) throws JsonProcessingException {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("UUID", uuid).put("PatientID", patientId).put("PatientEmail", patientEmail)
				.put("PatientFirstName", patientFirstName).put("PatientLastName", patientLastName);

		auditService.logAuditEvent(auditDetail, AuditEventType.PPE_INVITE_TO_PORTAL);
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public Optional<User> updatePatientDetailsFromOpen(Participant existingPatient) {
		existingPatient.setLastRevisedDate(LocalDateTime.now());
		return Optional.of(userRepository.save(existingPatient));
	}

	/*
	 * Helper method to format phone number as 10 digits without any other
	 * characters
	 */
	private String formatPhoneNumber(String phoneNumber) {
		StringBuilder formattedPhoneNumber = new StringBuilder();

		for (int index = 0; index < phoneNumber.length(); index++) {
			if (Character.isDigit(phoneNumber.charAt(index))) {
				formattedPhoneNumber.append(phoneNumber.charAt(index));
			}
		}
		String formattedNumber = formattedPhoneNumber.toString().substring(0, 10);
		log.info("Formatted Phone number is {}", formattedNumber);
		return formattedNumber;
	}

	/**
	 * Generate a Provider object with basic details.
	 * 
	 * @param ctepId
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param email
	 * @return
	 */
	private Provider generateBasicProviderDetails(Long ctepId, String firstName, String lastName, String phone,
			String email) {
		Provider provider = new Provider();
		provider.setOpenCtepID(ctepId);
		provider.setFirstName(firstName);
		provider.setLastName(lastName);
		provider.setPhoneNumber(formatPhoneNumber(phone));
		provider.setEmail(email);
		provider.setPreferredLanguage(LanguageOption.ENGLISH);
		log.info("Provider with Basic Details is {}", provider.toString());
		return provider;
	}

	/**
	 * Method to log details for audit purpose
	 * 
	 * @param userTypeId     - String representing the specific userId
	 * @param id             - Id for the user
	 * @param auditEventType - Insert event or Update Event
	 */
	private void raiseInsertParticipantAuditEvent(String userTypeId, String id, AuditEventType auditEventType) {
		ObjectNode auditDetail = mapper.createObjectNode();
		auditDetail.put(userTypeId, id);

		try {

			auditService.logAuditEvent(auditDetail, auditEventType);
		} catch (JsonProcessingException jsonProsException) {
			log.warn(jsonProsException.getMessage());
		}
	}

	/**
	 * Method to log update details for audit purpose.
	 * 
	 * @param oldKey         - Key for exisitng Provider/CRC ids
	 * @param newKey         - Key for new Provider/CRC ids
	 * @param oldIds         - Set of existing Provider/CRC ids
	 * @param newIds         - Set of new Provider/CRC ids
	 * @param patientId      - patientId from OPEN
	 * @param auditEventType - AuditEventType enum
	 */
	private void raiseUpdateParticipantAuditEvent(String oldKey, String newKey, Set<Long> oldIds, Set<Long> newIds,
			String patientId, AuditEventType auditEventType) {
		ObjectNode auditDetail = mapper.createObjectNode();
		auditDetail.put("PatientId", patientId);
		final AtomicInteger counter = new AtomicInteger(1);
		oldIds.forEach(id -> {
			auditDetail.put(oldKey + counter, Long.toString(id));
			counter.getAndAdd(1);
		});

		final AtomicInteger counter2 = new AtomicInteger(1);
		newIds.forEach(id -> {
			auditDetail.put(newKey + counter2, Long.toString(id));
			counter2.getAndAdd(1);
		});

		try {
			auditService.logAuditEvent(auditDetail, auditEventType);
		} catch (JsonProcessingException jsonProsException) {
			log.warn(jsonProsException.getMessage());
		}
	}

	/**
	 * Utility Method to extract existing ProviderIds that will be replaced with new
	 * ProviderIds
	 * 
	 * @param a - List of existing ProviderIds
	 * @param b - List of new ProviderIds
	 * @return map of existing and new ids that replaced the existing ones.
	 */
	private Map<String, Set<Long>> findDifferenceInProviders(Set<Long> a, Set<Long> b) {
		Set<Long> newDataSet = new HashSet<Long>();
		Set<Long> result = new HashSet<Long>(a);
		for (Long element : b) {
			// .add() returns false if element already exists
			if (!result.add(element)) {
				result.remove(element);
			} else {
				newDataSet.add(element);
			}
		}
		result.removeAll(newDataSet);
		Map<String, Set<Long>> mapOfPrviders = new HashMap<>();
		mapOfPrviders.put(NEW_PROVIDERS, newDataSet);
		mapOfPrviders.put("ExistingProviders", result);
		return mapOfPrviders;
	}

	/**
	 * Method to convert a Set of Providers into a Set of Long
	 * 
	 * @param providerSet - Set of providers
	 * @return a Set of Long openCtepIds
	 */
	private Set<Long> getProviderIds(Set<Provider> providerSet) {
		Set<Long> providerIds = new HashSet<Long>();
		providerSet.forEach(provider -> {
			providerIds.add(provider.getOpenCtepID());
		});
		return providerIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateUnreadReportReminderNotification(int daysUnread) {
		LocalDate today = LocalDate.now();

		LocalDateTime startOfPeriod = today.minusDays(daysUnread).atStartOfDay();
		LocalDateTime endOfPeriod = startOfPeriod.plusDays(1);

		log.info(today.toString() + ":Fetching Unread reports Uploaded between " + startOfPeriod.toString() + " and "
				+ endOfPeriod.toString());
		List<FileMetadata> uploadedFiles = fileService.getFilesUploadedBetween(
				codeRepository.findByCodeName(FileType.PPE_FILETYPE_BIOMARKER_REPORT.getFileType()), startOfPeriod,
				endOfPeriod);
		uploadedFiles.stream().forEach(file -> sendOverdueNotification(file));

	}

	private void sendOverdueNotification(FileMetadata fileMetadata) {

		log.debug("Sending notifications for file " + fileMetadata.getFileGUID() + " uploaded "
				+ fileMetadata.getDateUploaded());
		Participant patient = fileMetadata.getParticipant();
		CRC assocCRC = patient.getCrc();
		Set<Provider> associatedProviders = patient.getProviders();

		// Unread notification and email to patient.
		if (!fileMetadata.hasViewed(patient.getUserUUID())) {
			if (patient.isAllowEmailNotification()) {
				emailService.sendEmailToParticipantReminderUnreadReport(patient.getEmail(), patient.getFirstName(),
						patient.getPreferredLanguage());
				notificationService.notifyPatientReminderToReadBiomarkerReport(patient.getUserId());
			}
		}

		if (!fileMetadata.hasViewed(assocCRC.getUserUUID())) {
			if (assocCRC.isAllowEmailNotification()) {
				emailService.sendEmailToCRCAndProvidersReminderUnreadReport(assocCRC.getFirstName(),
						assocCRC.getEmail(), patient.getFullName(), assocCRC.getPreferredLanguage());
				notificationService.notifyProviderCRCReminderToReadBiomarkerReport(patient.getFullName(),
						assocCRC.getUserId(), patient.getPatientId());
			}
		}

		for (Provider provider : associatedProviders) {
			if (!fileMetadata.hasViewed(provider.getUserUUID())) {
				emailService.sendEmailToCRCAndProvidersReminderUnreadReport(provider.getFirstName(),
						provider.getEmail(), patient.getFullName(), provider.getPreferredLanguage());
				notificationService.notifyProviderCRCReminderToReadBiomarkerReport(patient.getFullName(),
						provider.getUserId(), patient.getPatientId());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> getUsersByRole(Set<Role> userRoles) {

		return userRepository.findByRoleIn(userRoles);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param crcUuid
	 * 
	 */
	@Override
	public Optional<User> updatePatientEmail(String patientId, String newEmail, String crcUuid)
			throws BusinessConstraintViolationException {
		Participant pa = participantRepository.findByPatientId(patientId).get();
		String previousEmail = pa.getEmail();
		if (StringUtils.isNotBlank(pa.getUserUUID())) {
			throw new BusinessConstraintViolationException("Patient has already activated. Email cannot be changed");
		}
		pa.setEmail(newEmail);
		pa = participantRepository.save(pa);
		raisePatientEmailUpdatedByCrcEvent(crcUuid, patientId, previousEmail, newEmail);
		return Optional.of(pa);
	}

	@Override
	public Optional<User> synchronizeUserEmailWithLogin(User user, String uuid, String newEmail) {
		final String previousEmail = user.getEmail();
		if (!newEmail.equalsIgnoreCase(previousEmail) && user.getUserUUID().equalsIgnoreCase(uuid)) {
			user.setEmail(newEmail);
			user = userRepository.save(user);
			raiseEmailUpdatedAtLoginGovEvent(uuid, previousEmail, newEmail);
		}
		return Optional.of(user);
	}

	private void raisePatientEmailUpdatedByCrcEvent(String uuid, String patientId, String previousEmail,
			String changedEmail) {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("CrcUUID", uuid).put("PatientId", patientId).put("PreviousEmail", previousEmail)
				.put("ChangedEmail", changedEmail);

		try {
			auditService.logAuditEvent(auditDetail, AuditEventType.PPE_EMAIL_MODIFIED);
		} catch (JsonProcessingException e) {
			log.warn(e.getMessage());
		}
	}

	private void raiseEmailUpdatedAtLoginGovEvent(String uuid, String previousEmail, String changedEmail) {
		ObjectNode auditDetail = mapper.createObjectNode();

		auditDetail.put("UUID", uuid).put("PreviousEmail", previousEmail).put("ChangedEmail", changedEmail);

		try {
			auditService.logAuditEvent(auditDetail, AuditEventType.PPE_EMAIL_MODIFIED);
		} catch (JsonProcessingException e) {
			log.warn(e.getMessage());
		}
	}

}
