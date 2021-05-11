package gov.nci.ppe.data.entity.dozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import org.junit.jupiter.api.Test;

import gov.nci.ppe.constants.PPERole;
import gov.nci.ppe.data.entity.GroupNotificationRequest;
import gov.nci.ppe.data.entity.dto.GroupNotificationRequestDto;
import gov.nci.ppe.data.entity.dto.MessageBody;
import gov.nci.ppe.data.entity.dto.Subject;

/**
 * Unit Test class for testing the dozer mapping between
 * {@link GroupNotificationSendRequestDto} to {@link GroupNotificationRequest}
 * 
 * @author PublicisSapient
 * 
 * @version 2.0
 *
 * @since Mar 27, 2021
 *
 */
public class GroupNotificationRequestDtoToGroupNotificationRequestMappingTest {

    @Test
    public void testGroupNotificationSendRequestDtoToGroupNotificationRequest() {
        GroupNotificationRequestDto source = new GroupNotificationRequestDto();
        source.setAudiences(Arrays.asList(PPERole.values()));
        MessageBody messageBody = new MessageBody();
        messageBody.setEn("English Msg");
        messageBody.setEs("Spanish Msg");
        source.setMessage(messageBody);
        Subject subject = new Subject();
        subject.setEn("English Subject");
        subject.setEs("Spanish Subject");
        source.setSubject(subject);

        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("dozer-mappings.xml").build();

        GroupNotificationRequest dest = mapper.map(source, GroupNotificationRequest.class);
        assertNotNull(dest);
        assertEquals(source.getAudiences().size(), dest.getRecipientRoles().size());
        assertEquals(source.getMessage().getEn(), dest.getMessageEnglish());
        assertEquals(source.getMessage().getEs(), dest.getMessageSpanish());
        assertEquals(source.getSubject().getEn(), dest.getSubjectEnglish());
        assertEquals(source.getSubject().getEs(), dest.getSubjectSpanish());
    }
}
