package gov.nci.ppe.services.impl;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Provider;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.JWTManagementService;
import gov.nci.ppe.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	protected Logger logger = Logger.getLogger(AuthorizationService.class.getName());

	@Autowired
	private JWTManagementService jwtMgmtService;

	@Autowired
	public UserService userService;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean authorize(String authToken, User user) {
		// If the Token is empty, the user is not authorized
		if (StringUtils.isEmpty(authToken)) {
			return false;
		}
		try {
			Jws<Claims> claims = jwtMgmtService.validateJWT(authToken);
			final String requestingUserUUID = (String) claims.getBody().get(JWTManagementService.TOKEN_CLAIM_USERNAME);
			logger.log(Level.INFO, requestingUserUUID);
			logger.log(Level.INFO, claims.toString());

			// Invalid JWT no username present
			if (StringUtils.isBlank(requestingUserUUID)) {
				logger.log(Level.WARNING, "No Username present in JWT");
				return false;
			}
			// If the UUID in the JWT matches the UUID of the targetUser, always allow
			if (requestingUserUUID.equalsIgnoreCase(user.getUserUUID())) {
				logger.log(Level.INFO, "User " + requestingUserUUID + " is requesting access to self");
				return true;
			}

			final String requestingUserRole = (String) claims.getBody().get(JWTManagementService.TOKEN_CLAIM_ROLE);

			switch (PPERole.valueOf(requestingUserRole)) {
			
			case ROLE_PPE_CRC:
				return authorizeCRC((Participant) user, requestingUserUUID);

			case ROLE_PPE_PROVIDER:
				return authorizeProvider((Participant) user, requestingUserUUID);

			case ROLE_PPE_MOCHA_ADMIN:
				return true;

			default:
				return false;
			}

		} catch (JwtException jwtException) {
			// Not a valid JWT, user is not authorized
			logger.log(Level.WARNING, jwtException.getMessage());
			return false;
		}
	}

	private boolean authorizeProvider(Participant targetUser, final String requestingUserUUID) {
		Optional<Provider> requestingProviderOptional = targetUser.getProviders().stream()
				.filter(provider -> requestingUserUUID.equals(provider.getUserUUID())).findAny();
		if (requestingProviderOptional.isEmpty()) {
			logger.log(Level.WARNING,
					"Provider " + requestingUserUUID + " denied access to patient " + targetUser.getUserUUID());
			return false;
		} else {
			logger.log(Level.INFO,
					"Provider " + requestingUserUUID + " allowed access to patient " + targetUser.getUserUUID());
			return true;
		}
	}

	private boolean authorizeCRC(Participant targetUser, final String requestingUserUUID) {
		if (targetUser.getCRC().getUserUUID().equalsIgnoreCase(requestingUserUUID)) {
			logger.log(Level.INFO,
					"CRC " + requestingUserUUID + " allowed access to patient " + targetUser.getUserUUID());
			return true;
		} else {
			logger.log(Level.WARNING,
					"CRC " + requestingUserUUID + " denied access to patient " + targetUser.getUserUUID());
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean authorize(String authToken, String targetUUID) {
		Optional<User> targetUserOptional = userService.findByUuid(targetUUID);
		if (targetUserOptional.isEmpty()) {
			logger.log(Level.WARNING, "No user found with UUID " + targetUUID);
			return false;
		} else {
			return authorize(authToken, targetUserOptional.get());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Optional<User> getRequestingUser(String authToken) {
		Jws<Claims> claims = jwtMgmtService.validateJWT(authToken);
		final String requestingUserUUID = (String) claims.getBody().get(JWTManagementService.TOKEN_CLAIM_USERNAME);
		logger.log(Level.INFO, requestingUserUUID);
		logger.log(Level.INFO, claims.toString());
		return userService.findByUuid(requestingUserUUID);
	}

}
