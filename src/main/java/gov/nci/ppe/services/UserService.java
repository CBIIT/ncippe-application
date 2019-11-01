package gov.nci.ppe.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.QuestionAnswer;
import gov.nci.ppe.data.entity.User;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
@Component
public interface UserService {

	/**
	 * This method fetches all the registered users and converts the User Object
	 * into UserDTO object
	 * 
	 * @return a String representing JSON Object
	 */
	public List<User> getAllRegisteredUsers();

	/**
	 * Fetch the User details based on GUID which is unique
	 * 
	 * @param userGUID - GUID of the user
	 * @return JSON format string containing user information.
	 */
	public Optional<User> findByUuid(String userGuid);

	/**
	 * This method will update the registered user's email notification preference
	 * and phone number.
	 * 
	 * @param userGUID               - GUID of the user
	 * @param phoneNumber            - the value can be empty string if the user
	 *                               wishes to delete his/her number.
	 * @param allowEmailNotification - value can be only 1 (true) or 2 (false)
	 * @return JSON String containing User information that was updated
	 */
	public Optional<User> updateUserDetails(String userGuid, Boolean userEmailNotification, String phoneNumber);

	/**
	 * Fetch the user based on the user's UserId
	 * 
	 * @param userId - UserId of the user.
	 * @return a User Object
	 */
	public Optional<User> findUserById(Long userId);

	/**
	 * Activates an User record by filling in the UUID value from login.gov.
	 * 
	 * @param userEmail - email of the user to activate
	 * @param userUUID  - UUID (from login.gov)
	 * @return - the updated User
	 */
	public Optional<User> activateUser(String userEmail, String userUUID);

	/**
	 * Changes the user's registered email address
	 * 
	 * @param userUUID  - UUID of the User
	 * @param userEmail - new email address to change to
	 * @return
	 */
	public Optional<User> updateEmail(String userUUID, String userEmail);

	/**
	 * Fetch the user based on their userGUID and PortalAccountStatus.
	 * 
	 * @param userGUID          - GUID of the user
	 * @param accountStatusList - List of potential Portal Account Status.
	 * @return a User Object
	 */
	public Optional<User> findByUuidAndPortalAccountStatus(String userGuid, List<String> accountStatusList);

	/**
	 * Update the portalAccountStatus of current user. The user can move from
	 * INITIATE to ACTIVE to INACTIVE.
	 * 
	 * @param userId                  - UserId of the user.
	 * @param portalAccountStatusCode - Code object containing the status to be
	 *                                updated to.
	 * @return a User Object whose status will be deactiviated
	 */
	public Optional<User> deactivateUserPortalAccountStatus(String userGuid);

	/**
	 * Withdraw patient participation from Biobank program.
	 * @param patient   - Participant object representing the patient who is been withdrawn from PPE
	 * @param qsAnsList - A list of survey questions and their answers provided during withdrawal
	 * @return          - a User Object whose status will be withdrawn from the program
	 */
	public Optional<User> withdrawParticipationFromBiobankProgram(Participant patient, List<QuestionAnswer> qsAnsList);

	/**
	 * Authorizes the User based on the idToken and email
	 * 
	 * @param email - email of the user to authorize
	 * @param uuid  - login.gov supplied uuid
	 * 
	 * @return The authorized user
	 */
	public Optional<User> authorizeUser(String email, String uuid);

	/**
	 * Decrypts the id_toen object from login.gov using logn.gov's pubic key
	 * 
	 * @param idToken
	 * @return
	 */
	public String decryptLoginGovToken(String idToken);

	/**
	 * Fetch the user based on their email and PortalAccountStatus.
	 * 
	 * @param email             - email of the user
	 * @param accountStatusList - List of potential Portal Account Status.
	 * @return a User Object
	 */
	public Optional<User> findByEmailAndPortalAccountStatus(String email, List<String> validAccountStatusList);

	/**
	 * Fetch the Participant based on their Patient ID and PortalAccountStatus.
	 * 
	 * @param patientId         - GUID of the user
	 * @param accountStatusList - List of potential Portal Account Status.
	 * @return a User Object
	 */
	public Optional<User> findByPatientIdAndPortalAccountStatus(String patientId, List<String> validAccountStatusList);

	/**
	 * Fetch a participant who is active in the bio bank program by their patient id
	 * 
	 * @param patientId - Patient number
	 * @return a User object
	 */
	public Optional<User> findActiveParticipantByPatientId(String patientId);
	
	/**
	 * Withdraw patient participation from Biobank program and sends an email to subscribers.
	 * @param patient   - Participant object representing the patient who is been withdrawn from PPE
	 * @param qsAnsList - A list of survey questions and their answers provided during withdrawal
	 * @return          - a User Object whose status will be withdrawn from the program
	 */
	public Optional<User> withdrawParticipationFromBiobankProgramAndSendNotification(Participant patient, List<QuestionAnswer> qsAnsList);
	
	/**
	 * Fetch a user by their email id
	 * @param emailId - Email id to search for the user
	 * @return a User Object
	 */
	public Optional<User> findByEmail(String emailId);
	
	/**
	 * Update the User record in database
	 * @param user - a User Object with updated values
	 * @return a User Object
	 */
	public Optional<User> updateUser(User user);

	/**
	 * Updates a Patient record to include patient name and email, changes state
	 * from NEW to INITIATED, and sends an invitation email to the patient.
	 * 
	 * @param patientId        - Unique Patient Id of the Patient
	 * @param uuid             - Unique login Id of the CRC performing the action
	 * @param patientEmail     - Email address of the patient (No format validation
	 *                         done at Service)
	 * @param patientFirstName - First Name of Patient
	 * @param patientLastName  - Last Name of Patient
	 * @return Updated Patient record
	 * @throws JsonProcessingException
	 */
	public Optional<User> invitePatientToPortal(String patientId, String uuid, String patientEmail,
			String patientFirstName, String patientLastName) throws JsonProcessingException;
}
