package gov.nci.ppe.services;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Interface for Audit Service to allow the PPE Portal to log audit events.
 * 
 * @author debsarka0
 *
 */
public interface AuditService {

	public void logAuditEvent(String eventDetails, String eventDetailType) throws JsonProcessingException;
}
