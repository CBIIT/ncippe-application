package gov.nci.ppe.services.impl;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.JWTManagementService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTManagementServiceImpl implements JWTManagementService {

	private static final Logger logger = LogManager.getLogger(JWTManagementServiceImpl.class);
	private SecretKey secretKey;

	@Autowired
	public JWTManagementServiceImpl(@Value("${app.jwt.secret}") byte[] secretKeyBytes) {
		secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createJWT(User user) {

		String signedJWT = Jwts.builder().claim(TOKEN_CLAIM_FIRSTNAME, user.getFirstName())
				.claim(TOKEN_CLAIM_USERNAME, user.getUserUUID()).claim(TOKEN_CLAIM_ROLE, user.getRole().getRoleName())
				.claim(TOKEN_CLAIM_LASTNAME, user.getLastName()).signWith(secretKey).compact();

		logger.debug("Generated JWT for UserId - {}, User Name - {}", user.getUserUUID(), user.getFullName());
		logger.debug("Token - {}", signedJWT);
		return signedJWT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Jws<Claims> validateJWT(String token) throws JwtException {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
	}

}
