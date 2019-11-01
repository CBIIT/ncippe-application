package gov.nci.ppe.auth.jwt;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class represents a user token
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-22
 */
public class UserToken {

	public static final String TOKEN_CLAIM_USERNAME = "username";
	public static final String TOKEN_CLAIM_ROLE = "role";
	public static final String TOKEN_CLAIM_FIRSTNAME = "firstname";
	public static final String TOKEN_CLAIM_LASTNAME = "lastname";

	private TokenUtil tokenUtil;

	private String username;
	private String role;
	private String firstName;
	private String lastName;

	// defaulting to null because it could be null and need it to not be undefined
	private Date expirationDate = null;

	private Builder token;

	/**
	 * Used for an outgoing Token. Populate the members of this class and call
	 * build() to create a JWT.
	 * 
	 * @param config GuidConfiguration object sharing local config
	 */
	public UserToken(TokenUtil tokenUtil) {
		this.tokenUtil = tokenUtil;
		this.token = tokenUtil.coreToken();
	}

	/**
	 * Used for an incoming Token. Allows the JWT to populate this object and use
	 * this object as a Model holding the claims as members. Automatically verifies
	 * the JWT and throws JWTVerificationException if it does not verify correctly
	 * 
	 * If the claims in this class are updated, a new JWT can be created using
	 * build()
	 * 
	 * @param config     GuidConfiguration object sharing local config
	 * @param inputToken JWT string token (without Bearer) identifying a User
	 * @throws JWTVerificationException if the JWT does not validate
	 */
	public UserToken(TokenUtil tokenUtil, String inputToken) throws JWTVerificationException {
		this(tokenUtil);
		DecodedJWT token = tokenUtil.verify(inputToken);
		username = token.getClaim(TOKEN_CLAIM_USERNAME).asString();
		role = token.getClaim(TOKEN_CLAIM_ROLE).asString();
		firstName = token.getClaim(TOKEN_CLAIM_FIRSTNAME).asString();
		lastName = token.getClaim(TOKEN_CLAIM_LASTNAME).asString();
	}

	public UserToken(TokenUtil tokenUtil, ObjectNode userDesc) {
		this.tokenUtil = tokenUtil;
		this.token = tokenUtil.coreToken();

		setUsername(userDesc.get(TOKEN_CLAIM_USERNAME).asText());
		setRole(userDesc.get(TOKEN_CLAIM_ROLE).asText());
		setFirstName(userDesc.get(TOKEN_CLAIM_FIRSTNAME).asText());
		setLastName(userDesc.get(TOKEN_CLAIM_LASTNAME).asText());
	}

	/**
	 * Builds, signs, and finalizes the token. Once this is run, the token is ready
	 * to be sent out
	 * 
	 * @return String full signed token
	 */
	public String build() {
		setExpires();
		token.withClaim(TOKEN_CLAIM_USERNAME, username).withClaim(TOKEN_CLAIM_ROLE, role)
				.withClaim(TOKEN_CLAIM_FIRSTNAME, firstName).withClaim(TOKEN_CLAIM_LASTNAME, lastName);
		return tokenUtil.sign(token);
	}

	/**
	 * Sets the expiration date to the token. This expiration time is set in
	 * constants
	 */
	public void setExpires() {
		Calendar date = Calendar.getInstance();
		long t = date.getTimeInMillis();
//		date.setTimeInMillis(t + Long.valueOf(env.getProperty("app.jwt.expirationMillis")));
		date.setTimeInMillis(t + Long.valueOf("1800000"));
		token.withExpiresAt(date.getTime());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expiration) {
		this.expirationDate = expiration;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("First Name: ").append(firstName).append(StringUtils.LF);
		builder.append("Last Name: ").append(lastName).append(StringUtils.LF);
		builder.append("Role: ").append(role).append(StringUtils.LF);
		builder.append("User Name: ").append(username).append(StringUtils.LF);
		return builder.toString();
	}
}
