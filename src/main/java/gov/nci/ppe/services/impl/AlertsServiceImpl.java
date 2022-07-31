package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.Alert;
import gov.nci.ppe.data.repository.AlertsRepository;
import gov.nci.ppe.services.AlertsService;

/**
 * Implementation class for {@link AlertsService}
 * 
 * @author PublicisSapient
 *
 * @version 2.6
 *
 * @since May 2, 2022
 */
@Service
public class AlertsServiceImpl implements AlertsService {

	private AlertsRepository alertsRepository;

	@Autowired
	public AlertsServiceImpl(AlertsRepository alertsRepository) {
		this.alertsRepository = alertsRepository;
	}

	@Override
	public List<Alert> getAlerts() {
		return alertsRepository.findByExpirationDateGreaterThanOrExpirationDateIsNull(LocalDateTime.now());
	}

}
