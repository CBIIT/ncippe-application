package gov.nci.ppe.services;

import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

/**
 * Service to create and validate Java Web Tokens
 * 
 * @author sarkard
 *
 */
@Component
public interface JWTManagementService {

	/**
	 * Creates a signed Java Web Token with the provided user's name and role
	 * 
	 * @param user - User to create authorization token for
	 * @return The signed token
	 */
	public String createJWT(User user);
	
	/**
	 * Validates an incoming token and returns the list of claims in the token
	 * 
	 * @param token - input JWT to validate and parse
	 * @return - List of claims in the token
	 * @throws JwtException - if the token is invalid
	 */
	public Jws<Claims> validateJWT(String token) throws JwtException;
}
