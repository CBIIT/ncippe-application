package gov.nci.ppe.services;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Interface for Audit Service to allow the PPE Portal to log audit events.
 * 
 * @author debsarka0
 *
 */
public interface AuditService {

	/**
	 * Method to raise an Audit event
	 * 
	 * @param eventDetails    - details of the event
	 * @param eventDetailType - type of event
	 * @throws JsonProcessingException
	 */
	public void logAuditEvent(String eventDetails, String eventDetailType) throws JsonProcessingException;
}
