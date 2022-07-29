package gov.nci.ppe.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.dto.CrcDTO;
import gov.nci.ppe.data.entity.dto.ParticipantDTO;
import gov.nci.ppe.services.AuditService;
import gov.nci.ppe.services.AuthorizationService;
import gov.nci.ppe.services.CodeService;
import gov.nci.ppe.services.UserService;

/**
 * Unit Test class for {@link UserController}
 * 
 * @author PublicisSapient
 *
 * @version 2.6
 *
 * @since Jul 28, 2022
 */
@ActiveProfiles("unittest")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

	@MockBean(name = "dozerBean")
	private Mapper mockDozerBeanMapper;

	@MockBean
	private UserService mockUserService;

	@MockBean
	private CodeService mockCodeService;

	@MockBean
	private AuditService mockAuditService;

	@MockBean
	private AuthorizationService mockAuthorizationService;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	private final String requestingUserUUID = UUID.randomUUID().toString();
	private final String targetUserUUID = UUID.randomUUID().toString();
	private final String newEmail = "newemail@example.com";

	@Test
	public void testUpdateUserEmail_Success() {
		Participant updatedUser = new Participant();
		Role participantRole = new Role();
		participantRole.setRoleName(PPERole.ROLE_PPE_PARTICIPANT.name());
		updatedUser.setRole(participantRole);
		updatedUser.setUserUUID(targetUserUUID);
		updatedUser.setEmail(newEmail);

		ParticipantDTO expectedUser = new ParticipantDTO();
		expectedUser.setUuid(targetUserUUID);
		expectedUser.setEmail(newEmail);

		CrcDTO crcDto = new CrcDTO();
		expectedUser.setCrc(crcDto);

		when(mockUserService.updateUserEmail(targetUserUUID, newEmail)).thenReturn(Optional.of(updatedUser));
		when(mockAuthorizationService.authorize(requestingUserUUID, targetUserUUID)).thenReturn(true);
		try {
			// String expectedUserString = mapper.writeValueAsString(expectedUser);
			when(mockDozerBeanMapper.map(any(Participant.class), eq(ParticipantDTO.class))).thenReturn(expectedUser);
			mockMvc.perform(post(UrlConstants.URL_USER_UPDATE_EMAIL, targetUserUUID, newEmail)
					.contentType(MediaType.TEXT_PLAIN_VALUE).header(CommonConstants.HEADER_UUID, requestingUserUUID))
					.andExpect(status().isOk()).andExpect(jsonPath("$.uuid", is(targetUserUUID)));
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testUpdateUserEmail_NotAuthorized() {
		when(mockAuthorizationService.authorize(requestingUserUUID, targetUserUUID)).thenReturn(false);
		try {
			mockMvc.perform(post(UrlConstants.URL_USER_UPDATE_EMAIL, targetUserUUID, newEmail)
					.contentType(MediaType.TEXT_PLAIN_VALUE).header(CommonConstants.HEADER_UUID, requestingUserUUID))
					.andExpect(status().isForbidden());
			verify(mockAuthorizationService).authorize(requestingUserUUID, targetUserUUID);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}

	}

}
