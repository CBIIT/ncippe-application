package gov.nci.ppe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dozermapper.core.Mapper;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import gov.nci.ppe.constants.CommonConstants;
import gov.nci.ppe.constants.HttpResponseConstants;
import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.GroupNotificationRequest;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.dto.GroupNotificationRequestDto;
import gov.nci.ppe.data.entity.dto.MessageBody;
import gov.nci.ppe.data.entity.dto.SubjectDto;
import gov.nci.ppe.services.impl.NotificationServiceImpl;
import gov.nci.ppe.services.impl.UserServiceImpl;

/**
 * Unit Test class for {@link NotificationController}.
 * 
 * @author PublicisSapient
 * @version 2.4;
 * @since 2021-05-17
 */

@ActiveProfiles("unittest")
@WebMvcTest(controllers = { NotificationController.class })
public class NotificationControllerTest {

	@MockBean
	private NotificationServiceImpl mockNotificationService;

	@MockBean
	private MessageSource mockMessageSource;

	@MockBean
	private UserServiceImpl mockUserService;

	@MockBean(name = "dozerBean")
	private Mapper mockDozerBeanMapper;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	private final String requestingUserUUID = UUID.randomUUID().toString();

	@Test
	public void testSendNotificationRequesterNotFound() {
		String request = "Dummy Request";
		when(mockUserService.findByUuid(requestingUserUUID)).thenReturn(Optional.empty());
		when(mockMessageSource.getMessage(eq(HttpResponseConstants.NO_USER_FOUND_MSG), any(), any()))
				.thenReturn("No User Found");
		try {
			mockMvc.perform(post(UrlConstants.URL_NOTIFICATIONS).contentType(MediaType.TEXT_PLAIN_VALUE)
					.content(request).header(CommonConstants.HEADER_UUID, requestingUserUUID))
					.andExpect(status().isNotFound());
			verify(mockUserService).findByUuid(requestingUserUUID);
			verify(mockMessageSource).getMessage(eq(HttpResponseConstants.NO_USER_FOUND_MSG), any(), any());
			verifyNoMoreInteractions(mockNotificationService, mockDozerBeanMapper, mockMessageSource, mockUserService);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSendNotificationRequesterNotAuthorized() {
		String request = "Dummy Request";
		Participant participant = new Participant();
		Role role = new Role();
		role.setRoleName(PPERole.ROLE_PPE_PARTICIPANT.name());
		participant.setRole(role);
		when(mockUserService.findByUuid(anyString())).thenReturn(Optional.of(participant));
		when(mockMessageSource.getMessage(eq(HttpResponseConstants.UNAUTHORIZED_ACCESS), any(), any()))
				.thenReturn("Not Authorized");
		try {
			mockMvc.perform(post(UrlConstants.URL_NOTIFICATIONS).contentType(MediaType.TEXT_PLAIN_VALUE)
					.content(request).header(CommonConstants.HEADER_UUID, requestingUserUUID))
					.andExpect(status().isForbidden());
			verify(mockUserService).findByUuid(requestingUserUUID);
			verify(mockMessageSource).getMessage(eq(HttpResponseConstants.UNAUTHORIZED_ACCESS), any(), any());
			verifyNoMoreInteractions(mockNotificationService, mockDozerBeanMapper, mockMessageSource, mockUserService);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSendNotificationRequesterSuccess() {
		try {
			GroupNotificationRequestDto requestDto = new GroupNotificationRequestDto();
			requestDto.setAudiences(Arrays.asList(PPERole.ROLE_PPE_PARTICIPANT, PPERole.ROLE_PPE_PROVIDER));
			MessageBody msgBody = new MessageBody();
			msgBody.setEn("English Message");
			SubjectDto subject = new SubjectDto();
			subject.setEn("English Subject");
			requestDto.setMessage(msgBody);
			requestDto.setSubject(subject);
			String requestString = mapper.writeValueAsString(requestDto);

			GroupNotificationRequest request = new GroupNotificationRequest();

			Participant requester = new Participant();
			requester.setUserUUID(requestingUserUUID);
			Role role = new Role();
			role.setRoleName(PPERole.ROLE_PPE_MESSENGER.name());
			requester.setRole(role);

			when(mockUserService.findByUuid(requestingUserUUID)).thenReturn(Optional.of(requester));
			when(mockDozerBeanMapper.map(any(GroupNotificationRequestDto.class), eq(GroupNotificationRequest.class)))
					.thenReturn(request);
			ArgumentCaptor<GroupNotificationRequest> captor = ArgumentCaptor.forClass(GroupNotificationRequest.class);
			mockMvc.perform(post(UrlConstants.URL_NOTIFICATIONS).contentType(MediaType.TEXT_PLAIN_VALUE)
					.content(requestString).header(CommonConstants.HEADER_UUID, requestingUserUUID))
					.andExpect(status().isCreated());
			verify(mockUserService).findByUuid(anyString());
			verify(mockDozerBeanMapper).map(any(GroupNotificationRequestDto.class), eq(GroupNotificationRequest.class));
			verify(mockNotificationService).sendGroupNotifications(captor.capture());
			assertEquals(request, captor.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage(), e);
		}
	}

}
