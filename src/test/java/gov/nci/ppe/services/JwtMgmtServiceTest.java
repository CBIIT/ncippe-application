package gov.nci.ppe.services;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.services.impl.JWTManagementServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("unittest")
public class JwtMgmtServiceTest {
	
	@Autowired
	private JWTManagementServiceImpl jwtMgmtService; 
	
	@Test
	public void testKeyGenerationAndValidation() {
		User user = new User();
		user.setUserUUID(UUID.randomUUID().toString());
		user.setFirstName("FirstTest");
		user.setLastName("LastTest");
		Role userRole = new Role();
		userRole.setRoleName("TestRole");
		user.setRole(userRole);
		
		String jwt = jwtMgmtService.createJWT(user);
		assertNotNull(jwt);
		System.out.println(jwt);
		
		Jws<Claims> claims = jwtMgmtService.validateJWT(jwt);
		assertNotNull(claims);
		System.out.println("Claims "+claims.toString());
	}

}
