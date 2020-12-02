package gov.nci.ppe.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.NCORPSite;
import gov.nci.ppe.data.repository.NCORPSiteRepository;
import gov.nci.ppe.services.NCORPSiteService;

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

	/*
	 * {@inheritDoc}
	 */
	@Override
	public NCORPSite getSite(String siteCtepId) {
		Optional<NCORPSite> siteOpt = siteRepo.findByCTEPId(siteCtepId);
		NCORPSite matchingSite = null;
		if (siteOpt.isPresent()) {
			matchingSite = siteOpt.get();
		}
		return matchingSite;
	}

}
