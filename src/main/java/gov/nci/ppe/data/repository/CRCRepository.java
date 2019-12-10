package gov.nci.ppe.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.CRC;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-11-14
 */
public interface CRCRepository extends JpaRepository<CRC, Long> {
	
	/**
	 * Find CRC based on the CTEP ID column
	 * @param openCtepId
	 * @return An optional User object
	 */
	Optional<CRC> findCRCByOpenCtepID(Long openCtepId);
}
