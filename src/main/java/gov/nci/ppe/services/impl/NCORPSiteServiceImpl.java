package gov.nci.ppe.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.NCORPSite;
import gov.nci.ppe.data.repository.NCORPSiteRepository;
import gov.nci.ppe.services.NCORPSiteService;

/**
 * 
 */
@Service
public class NCORPSiteServiceImpl implements NCORPSiteService {

	@Autowired
	private NCORPSiteRepository siteRepo;

	@Override
	public List<NCORPSite> getAllActiveSites() {


		return siteRepo.findByActiveTrue();

	}

}
