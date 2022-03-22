package gov.nci.ppe.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nci.ppe.constants.CommonConstants.AuditEventType;
import gov.nci.ppe.data.entity.dto.AuditEventDto;

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
	public void logAuditEvent(ObjectNode eventDetails, AuditEventType eventDetailType) throws JsonProcessingException;

	/**
	 * Log the {@link AuditEventDto} object in the Audit Logs
	 * 
	 * @param auditEventDto - details of the the audit event
	 */
	public void logAuditEvent(AuditEventDto auditEventDto) throws JsonProcessingException;
}
