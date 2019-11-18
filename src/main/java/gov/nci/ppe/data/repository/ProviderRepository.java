package gov.nci.ppe.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.Provider;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-11-14
 */
public interface ProviderRepository extends JpaRepository<Provider, Long> {
	
	/**
	 * Find providers based on the CTEP ID column
	 * @param openCtepId
	 * @return An optional User object
	 */
	Optional<Provider> findProviderByOpenCtepID(Long openCtepId);
}
