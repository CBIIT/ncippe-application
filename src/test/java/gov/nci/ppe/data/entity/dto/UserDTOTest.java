package gov.nci.ppe.data.entity.dto;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserDTOTest {

	@InjectMocks
	private UserDTO userDTO;

	@Mock
	private PortalNotificationDTO mPortalNotificationDTO;

	private List<PortalNotificationDTO> notificationList = new ArrayList<>();

	private String firstName;
	private String lastName;
	private String email;
	private String userUUID;
	private String phoneNumber;
	private boolean allowEmailNotification;
	private String userType;
	private String roleName;
	private Timestamp dateCreated;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		notificationList.add(mPortalNotificationDTO);
		firstName = "John";
		lastName = "Doe";
		email = "testEmail@test.com";
		userUUID = "TEST1234";
		phoneNumber = "0123456789";
		allowEmailNotification = true;
		userType = "1";
		roleName = "Admin";
		dateCreated = new Timestamp(System.currentTimeMillis());
	}

	@Test
	public void testSetterGetter() {
		userDTO.setFirstName(firstName);
		userDTO.setLastName(lastName);
		userDTO.setEmail(email);
		userDTO.setUuid(userUUID);
		userDTO.setPhoneNumber(phoneNumber);
		userDTO.setAllowEmailNotification(allowEmailNotification);
		userDTO.setUserType(userType);
		userDTO.setRoleName(roleName);
		userDTO.setDateCreated(dateCreated);
		userDTO.setNotifications(notificationList);
		assertEquals(firstName, userDTO.getFirstName());
		assertEquals(lastName, userDTO.getLastName());
		assertEquals(email, userDTO.getEmail());
		assertEquals(userUUID, userDTO.getUuid());
		assertEquals(phoneNumber, userDTO.getPhoneNumber());
		assertEquals(allowEmailNotification, userDTO.getAllowEmailNotification());
		assertEquals(userType, userDTO.getUserType());
		assertEquals(roleName, userDTO.getRoleName());
		assertEquals(dateCreated, userDTO.getDateCreated());
		assertEquals(notificationList, userDTO.getNotifications());
	}

}
