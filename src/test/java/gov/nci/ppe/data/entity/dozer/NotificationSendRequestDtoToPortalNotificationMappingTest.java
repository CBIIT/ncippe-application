package gov.nci.ppe.data.entity.dozer;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;


import org.junit.jupiter.api.Test;

import gov.nci.ppe.data.entity.PortalNotification;
import gov.nci.ppe.data.entity.dto.NotificationSendRequestDto;

/**
 * Unit Test class for testing the dozer mapping between
 * {@link NotificationSendRequestDto} to {@link PortalNotification}
 * 
 * @author PublicisSapient
 * 
 * @version 2.0
 *
 * @since Mar 27, 2021
 *
 */
public class NotificationSendRequestDtoToPortalNotificationMappingTest {

    @Test
    public void testNotificationSendRequestDtoToPortalNotification() {
        NotificationSendRequestDto source = new NotificationSendRequestDto();
        
        Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("src/main/resources/dozer-mappings.xml").build();
        
        PortalNotification dest = mapper.map(source, PortalNotification.class);
        assertNotNull(dest);

        
    }
}
