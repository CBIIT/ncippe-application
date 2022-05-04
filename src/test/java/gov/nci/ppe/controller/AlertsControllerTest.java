package gov.nci.ppe.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.github.dozermapper.core.Mapper;

import gov.nci.ppe.constants.CommonConstants.AlertContentType;
import gov.nci.ppe.constants.UrlConstants;
import gov.nci.ppe.data.entity.Alert;
import gov.nci.ppe.data.entity.dto.AlertDto;
import gov.nci.ppe.data.entity.dto.MessageBody;
import gov.nci.ppe.services.impl.AlertsServiceImpl;

/**
 * Unit Test Class for {@link AlertsController}
 * 
 * @author PublicisSapient
 * @version 2.6;
 * @since 2022-05-02
 */
@ActiveProfiles("unittest")
@WebMvcTest(controllers = AlertsController.class)
public class AlertsControllerTest {

	@MockBean
	private AlertsServiceImpl mockAlertsService;

	@MockBean(name = "dozerBean")
	private Mapper mockDozerBeanMapper;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testGetAlerts() {

		Alert expectedAlert = new Alert();
		expectedAlert.setId(1);
		expectedAlert.setContentType(AlertContentType.ERROR);
		expectedAlert.setDateCreated(LocalDateTime.now());
		expectedAlert.setExpirationDate(null);
		expectedAlert.setContentType(AlertContentType.ERROR);
		expectedAlert.setDateCreated(LocalDateTime.now());
		expectedAlert.setExpirationDate(LocalDateTime.now());
		expectedAlert.setMessageEnglish("English Alert");
		expectedAlert.setMessageSpanish("Alerta Espanola");

		AlertDto expected = new AlertDto();
		expected.setId(1);
		expected.setContentType(AlertContentType.ERROR);
		expected.setDateCreated(Timestamp.valueOf(expectedAlert.getDateCreated()));
		expected.setExpirationDate(Timestamp.valueOf(expectedAlert.getExpirationDate()));
		MessageBody message = new MessageBody();
		message.setEn(expectedAlert.getMessageEnglish());
		message.setEs(expectedAlert.getMessageSpanish());
		expected.setMessage(message);
		List<Alert> expectedAlerts = new ArrayList<>();
		expectedAlerts.add(expectedAlert);
		when(mockAlertsService.getAlerts()).thenReturn(expectedAlerts);
		when(mockDozerBeanMapper.map(any(Alert.class), eq(AlertDto.class))).thenReturn(expected);
		try {
			mockMvc.perform(get(UrlConstants.URL_ALERTS)).andExpect(status().isOk())
					.andExpect(jsonPath("$[0].id").value(expected.getId()))
					.andExpect(jsonPath("$[0].contentType").value(expected.getContentType().getContentType()))
					.andExpect(jsonPath("$[0].dateCreated").value(expected.getDateCreated().getTime()))
					.andExpect(jsonPath("$[0].expirationDate").value(expected.getExpirationDate().getTime()))
					.andExpect(jsonPath("$[0].message.en").value(expected.getMessage().getEn()))
					.andExpect(jsonPath("$[0].message.es").value(expected.getMessage().getEs()));
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
}
