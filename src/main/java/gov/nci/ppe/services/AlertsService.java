package gov.nci.ppe.services;

import java.util.List;

import gov.nci.ppe.data.entity.Alert;

/**
 * Interface defining the operations for Alerts related functionality
 * 
 * @author PublicisSapient
 *
 * @version 2.6
 *
 * @since May 2, 2022
 */
public interface AlertsService {

	/**
	 * Return list of Alerts that are currently unexpired
	 * 
	 * @return list of {@link Alert} entities
	 */
	List<Alert> getAlerts();
}
