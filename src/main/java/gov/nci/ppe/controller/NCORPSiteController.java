package gov.nci.ppe.controller;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.data.entity.NCORPSite;
import gov.nci.ppe.data.entity.dto.NCORPSiteDTO;
import gov.nci.ppe.services.NCORPSiteService;
import io.swagger.annotations.ApiOperation;

/**
 * Rest Endpoint for returning NCORP Site information
 * 
 * @author PublicisSapient
 * 
 * @version 2.0
 *
 * @since Jan 29, 2020
 *
 */
@RestController
public class NCORPSiteController {

	private static final String NO_SITE_FOUND_MSG = "{\n\"error\" : \"No active Sites found \"\n}";

	@Autowired
	private NCORPSiteService siteService;

	@Autowired
	@Qualifier("dozerBean")
	private Mapper dozerBeanMapper;

	@ApiOperation(value = "Return all the active sites.")
	@GetMapping(value = "/publicapi/v1/sites")
	public ResponseEntity<String> getAllActiveSites() throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", CommonConstants.APPLICATION_CONTENTTYPE_JSON);
		List<NCORPSite> sitesList = siteService.getAllActiveSites();
		// If there are no registered users, return back no content
		if (CollectionUtils.isEmpty(sitesList)) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(NO_SITE_FOUND_MSG);
		}

		String responseString = convertSitesToJSON(sitesList);
		return new ResponseEntity<String>(responseString, httpHeaders, HttpStatus.OK);

	}

	private String convertSitesToJSON(List<NCORPSite> sitesList) throws JsonProcessingException {
		List<NCORPSiteDTO> sites = new ArrayList<>();
		sitesList.forEach(site -> {
			sites.add(dozerBeanMapper.map(site, NCORPSiteDTO.class));
		});
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(sites);
	}
}
