package gov.nci.ppe.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.nci.ppe.data.entity.NCORPSite;

/**
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2020-01-27
 *
 */
public interface NCORPSiteRepository extends JpaRepository<NCORPSite, Long> {

	/**
	 * Returns list of Active NCORP Sites
	 * 
	 * @return List of Sites with the Active status
	 */
	public List<NCORPSite> findByActiveTrue();
}
