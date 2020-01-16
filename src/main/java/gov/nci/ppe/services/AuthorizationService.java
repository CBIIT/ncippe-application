package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.Participant;

@Component
public interface AuthorizationService {

	public boolean authorize(String authToken, Participant targetUser);

	public boolean authorize(String authToken, String targetUUID);
}
