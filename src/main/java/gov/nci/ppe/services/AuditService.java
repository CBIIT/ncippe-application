package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

/**
 * Interface for Audit Service to allow the PPE Portal to log audit events.
 * 
 * @author debsarka0
 *
 */
@Component
public interface AuditService {

	public void logAuditEvent(String eventDetails, String eventDetailType);
}
