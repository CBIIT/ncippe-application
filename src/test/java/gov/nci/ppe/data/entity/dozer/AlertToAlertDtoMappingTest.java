package gov.nci.ppe.data.entity.dozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants.AlertContentType;
import gov.nci.ppe.data.entity.Alert;
import gov.nci.ppe.data.entity.dto.AlertDto;

public class AlertToAlertDtoMappingTest {

	@Test
	public void testAlertToAlertDtoMapping() {
		Alert source = new Alert();
		source.setId(1);

		source.setContentType(AlertContentType.ERROR);
		source.setDateCreated(LocalDateTime.now());
		source.setExpirationDate((LocalDateTime.now()));
		source.setMessageEnglish("English Alert");
		source.setMessageSpanish("Alerta Espanola");

		Mapper mapper = DozerBeanMapperBuilder.create().withMappingFiles("dozer-mappings.xml").build();
		AlertDto dest = mapper.map(source, AlertDto.class);
		assertNotNull(dest);
		assertEquals(source.getId(), dest.getId());
		assertEquals(source.getContentType(), dest.getContentType());
		assertEquals(Timestamp.valueOf(source.getDateCreated()), dest.getDateCreated());
		assertEquals(Timestamp.valueOf(source.getExpirationDate()), dest.getExpirationDate());
		assertEquals(source.getMessageEnglish(), dest.getMessage().getEn());
		assertEquals(source.getMessageSpanish(), dest.getMessage().getEs());
	}
}