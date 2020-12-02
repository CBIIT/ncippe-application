package gov.nci.ppe.services;

import java.util.List;

import gov.nci.ppe.data.entity.NCORPSite;

public interface NCORPSiteService {

	/**
	 * Returns all active NCORP Sites.
	 * 
	 * @return List of Active NCORP Sites
	 */
	public List<NCORPSite> getAllActiveSites();

	/**
	 * Fetches the Site by its CTEP ID
	 * 
	 * @param siteCtepId
	 * @return
	 */
	public NCORPSite getSite(String siteCtepId);

}
