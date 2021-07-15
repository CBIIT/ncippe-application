package gov.nci.ppe.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.NCORPSite;
import gov.nci.ppe.data.repository.NCORPSiteRepository;
import gov.nci.ppe.services.NCORPSiteService;

/**
 * Implementation of {@link NCORPSiteService}
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Jan 30, 2020
 *
 */
@Service
public class NCORPSiteServiceImpl implements NCORPSiteService {

	private NCORPSiteRepository siteRepo;

	@Autowired
	public NCORPSiteServiceImpl(NCORPSiteRepository siteRepo) {
		this.siteRepo = siteRepo;
	}

	/*
	 * {@inheritDoc}
	 */
	@Override
	public List<NCORPSite> getAllActiveSites() {

		return siteRepo.findByActiveTrue();

	}

}
