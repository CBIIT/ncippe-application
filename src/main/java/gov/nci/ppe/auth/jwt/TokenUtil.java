package gov.nci.ppe.auth.jwt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * This class generates the JWT 
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-07-22 
 */
@Component
@Scope("application")
public class TokenUtil {

	private final static Logger logger = LoggerFactory.getLogger(TokenUtil.class);

	private Algorithm algorithm;
	private RSAPublicKey publicKey;
	private RSAPrivateCrtKey privateKey;
	private JWTVerifier verifier;

	@Autowired
    private Environment env;
	
	private TokenUtil() {

	}

	/**
	 * This method initializes the TokenUtil object.
	 */
	@PostConstruct
	public void init() {
		generateKeyPair();
		buildAlgorithm();
		buildVerifier();
		logger.debug("Token Utility initialization complete");
	}

	
	/**
	 * @param tokenClaimsBuilder
	 * @return
	 */
	public String sign(Builder tokenClaimsBuilder) {
		return tokenClaimsBuilder.sign(algorithm);
	}

	/**
	 * @param token
	 * @return com.auth0.jwt.interfaces.DecodedJWT
	 * @throws JWTVerificationException
	 */
	public DecodedJWT verify(String token) throws JWTVerificationException {
		return verifier.verify(token);
	}

	/**
	 * @return An object of Builder class that holds the claims
	 */
	public Builder coreToken() {
		Map<String, Object> headerClaims = new HashMap<>();
		headerClaims.put("alg", env.getProperty("app.jwt.algName"));
		headerClaims.put("typ", env.getProperty("app.jwt.tokenType"));
		return JWT.create().withHeader(headerClaims).withIssuer(env.getProperty("app.jwt.issuer"));
	}

	private void buildAlgorithm() {
		algorithm = Algorithm.RSA512(publicKey, privateKey);
	}

	private void buildVerifier() {
		verifier = JWT.require(algorithm).withIssuer(env.getProperty("app.jwt.issuer")).build();
	}

	private void generateKeyPair() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048, new SecureRandom());
			KeyPair kp = kpg.generateKeyPair();
			privateKey = (RSAPrivateCrtKey) kp.getPrivate();
			publicKey = (RSAPublicKey) kp.getPublic();
			logger.info("new keypair generated for this session");
		}
		catch(NoSuchAlgorithmException e) {
			// TODO: insert default keys.
			// Until the above is done, any time this exception is thrown, no tokens can be created

			logger.error("Failed to generate the security keypair.  Falling back to a pre-generated key set");
			e.printStackTrace();
		}
	}
}
