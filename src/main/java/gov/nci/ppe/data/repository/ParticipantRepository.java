package gov.nci.ppe.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-29
 */
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
	/**
	 * Returns an User entity matching the supplied patient id and
	 * PortalAccountStatus
	 * 
	 * @param patientId         - OPEN Id of the User to search for
	 * @param accountStatusList - List of possible Account Status
	 * @return Participant object found.
	 */
	Optional<User> findByPatientIdAndPortalAccountStatusIn(String patientId, List<Code> accountStatusCodeList);

	/**
	 * Returns active Patient Record matching the provided patient id
	 * 
	 * @param patientId - OPEN Id of the User to search for
	 * @return {@link Participant} object found
	 */
	Optional<User> findByPatientIdAndIsActiveBiobankParticipantTrue(String patientId);

	/**
	 * Returns Patient Record matching the provided patient id
	 * 
	 * @param patientId - OPEN Id of the User to search for
	 * @return {@link Participant} object found
	 */
	Optional<Participant> findByPatientId(String patientId);

}
