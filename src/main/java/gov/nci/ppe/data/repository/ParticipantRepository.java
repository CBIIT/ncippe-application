package gov.nci.ppe.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
	/**
	 * Returns an User entity matching the supplied patient id and
	 * PortalAccountStatus
	 * 
	 * @param patientId         - Email of the User to search for
	 * @param accountStatusList - List of possible Account Status
	 * @return User object found.
	 */
	Optional<User> findByPatientIdAndPortalAccountStatusIn(String patientId, List<Code> accountStatusCodeList);

	Optional<User> findByPatientIdAndIsActiveBiobankParticipantTrue(String patientId);

}
