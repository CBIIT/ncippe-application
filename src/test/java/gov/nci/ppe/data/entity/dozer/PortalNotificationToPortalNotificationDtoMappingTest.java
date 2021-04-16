package gov.nci.ppe.data.entity.dozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import org.junit.jupiter.api.Test;

import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.dto.PortalNotificationDTO;

public class PortalNotificationToPortalNotificationDtoMappingTest {
    
    @Test
    public void testPortalNotificationToPortalNotificationDtoMapping(){
        PortalNotification source = new PortalNotification();

        source.setDateGenerated(LocalDateTime.now());
        source.setMessageEnglish("messageEnglish");
        source.setMessageFrom("messageFrom");
        source.setMessageSpanish("messageSpanish");
        source.setPortalNotificationId(100L);
        source.setSubjectEnglish("subjectEnglish");
        source.setSubjectSpanish("subjectSpanish");
        source.setUserId(101L);
        source.setViewedByUser(0);

        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("dozer-mappings.xml").build();

        PortalNotificationDTO destination = mapper.map(source, PortalNotificationDTO.class);

        assertNotNull(destination);
        assertEquals(source.getMessageEnglish(), destination.getMessage().getEn());
        assertEquals(source.getMessageSpanish(), destination.getMessage().getEs());
        assertEquals(source.getSubjectEnglish(), destination.getSubject().getEn());
        assertEquals(source.getSubjectSpanish(), destination.getSubject().getEs());
    }
}
