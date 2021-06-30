package gov.nci.ppe.data.entity.dozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.GroupNotificationRequest;
import gov.nci.ppe.data.entity.Role;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.entity.dto.GroupNotificationHistoryRecordDto;

/**
 * Unit Test class for testing the dozer mapping between
 * {@link GroupNotificationRequest} and
 * {@link GroupNotificationHistoryRecordDto}
 * 
 * @author PublicisSapient
 * 
 * @version 2.3
 *
 * @since Jun 29, 2021
 *
 */
public class GroupNotificationRequestToGroupNotificationHistoryRecordDtoMappingTest {

	private static final String msgEn = "English Message";
	private static final String msgEs = "Spanish Message";
	private static final String subjEn = "English Subject";
	private static final String subjEs = "Spanish Subject";
	private static final String firstName = "First";
	private static final String lastName = "Last";
	private static final String email = "test@example.com";
	private static final UUID userId = UUID.randomUUID();

	@Test
	public void testGroupNotificationRequestToGroupNotificationHistoryRecordDtoMapping() {

		GroupNotificationRequest source = new GroupNotificationRequest();
		source.setRecipientRoles(Arrays.stream(PPERole.values()).map(ppeRole -> {
			Role role = new Role();
			role.setDescription(ppeRole.name());
			return role;
		}).collect(Collectors.toSet()));

		User requester = new User();
		requester.setEmail(email);
		requester.setUserUUID(userId.toString());
		requester.setLastName(lastName);
		requester.setFirstName(firstName);
		source.setRequester(requester);
		source.setMessageEnglish(msgEn);
		source.setMessageSpanish(msgEs);
		source.setSubjectEnglish(subjEn);
		source.setSubjectSpanish(subjEs);
		source.setTimeOfRequest(LocalDateTime.now());

		Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("dozer-mappings.xml").build();

		GroupNotificationHistoryRecordDto dest = mapper.map(source, GroupNotificationHistoryRecordDto.class);
		assertNotNull(dest);
		assertEquals(msgEn, dest.getMessage().getEn());
		assertEquals(msgEs, dest.getMessage().getEs());
		assertEquals(subjEn, dest.getSubject().getEn());
		assertEquals(subjEs, dest.getSubject().getEs());
		assertEquals(dest.getAudiences().size(), source.getRecipientRoles().size());
		assertNotNull(dest.getMessageFrom());
		assertEquals(email, dest.getMessageFrom().getEmail());
		assertEquals(firstName, dest.getMessageFrom().getFirstName());
		assertEquals(lastName, dest.getMessageFrom().getLastName());
		assertEquals(userId.toString(), dest.getMessageFrom().getUserUUID());
		assertNotNull(dest.getDateSent());
	}
}
