package gov.nci.ppe.data.entity.dozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants.NewsEventType;
import gov.nci.ppe.data.entity.NewsEvent;
import gov.nci.ppe.data.entity.dto.NewsEventDto;

/**
 * Unit Test class for testing the dozer mapping between {@link NewsEvent} to
 * {@link NewsEventDto}
 * 
 * @author PublicisSapient
 * 
 * @version 2.6
 *
 * @since Mar 23, 2022
 *
 */

public class NewsEventToNewsEventDtoMappingTest {

	@Test
	public void testNewsEventToNewsEventDtoMapping() {

		NewsEvent source = new NewsEvent();
		source.setId(-1L);
		source.setAuthor("Author");
		source.setContentType(NewsEventType.NEWS);
		source.setExpirationDate(LocalDateTime.now());
		source.setLink("Http://google.com");

		Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("dozer-mappings.xml").build();

		NewsEventDto dest = mapper.map(source, NewsEventDto.class);
		assertNotNull(dest);
		assertEquals(source.getAuthor(), dest.getAuthor());
		assertEquals(source.getContentType().getNewsEventType(), dest.getContentType());
		assertEquals(source.getExpirationDate(), dest.getExpirationDate().toLocalDateTime());
		assertEquals(source.getId(), dest.getId());
		assertEquals(source.getLink(), dest.getLink());

	}

}
