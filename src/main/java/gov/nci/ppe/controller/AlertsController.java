package gov.nci.ppe.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.Alert;
import gov.nci.ppe.data.entity.dto.AlertDto;
import gov.nci.ppe.services.AlertsService;
import io.swagger.annotations.ApiOperation;

/**
 * Implements a ReST endpoint to return any active alerts
 * 
 */

@RestController
public class AlertsController {

	private AlertsService alertsService;

	private Mapper dozerBeanMapper;

	@Autowired
	public AlertsController(AlertsService alertsService, @Qualifier("dozerBean") Mapper dozerBeanMapper) {
		this.alertsService = alertsService;
		this.dozerBeanMapper = dozerBeanMapper;
	}

	@ApiOperation(value = "Method to return list of active alerts")
	@GetMapping(value = UrlConstants.URL_ALERTS, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAlerts() throws JsonProcessingException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		List<Alert> alerts = alertsService.getAlerts();
		String alertsString = convertAlertsToJson(alerts);
		return new ResponseEntity<String>(alertsString, httpHeaders, HttpStatus.OK);
	}

	private String convertAlertsToJson(List<Alert> alerts) throws JsonProcessingException {
		List<AlertDto> alertDtos = alerts.stream().map(alert -> dozerBeanMapper.map(alert, AlertDto.class))
				.collect(Collectors.toList());
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(alertDtos);
	}

}