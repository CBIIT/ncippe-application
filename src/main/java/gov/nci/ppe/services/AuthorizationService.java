package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.User;

@Component
public interface AuthorizationService {

	public boolean authorize(String authToken, User targetUser);

	public boolean authorize(String authToken, String targetUUID);
}
