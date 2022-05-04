package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.data.entity.Alert;
import gov.nci.ppe.data.repository.AlertsRepository;
import gov.nci.ppe.services.impl.AlertsServiceImpl;

@ActiveProfiles("unittest")
@Tag("service")
public class AlertsServiceImplTest {

	@InjectMocks
	private AlertsServiceImpl classUnderTest;

	@Mock
	private AlertsRepository alertsRepository;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void tesGetAlerts_Succes() {

		List<Alert> alerts = new ArrayList<>();
		when(alertsRepository.findByExpirationDateGreaterThanOrExpirationDateIsNull(any(LocalDateTime.class)))
				.thenReturn(alerts);
		List<Alert> results = classUnderTest.getAlerts();
		assertNotNull(results);
		verify(alertsRepository).findByExpirationDateGreaterThanOrExpirationDateIsNull(any(LocalDateTime.class));
	}

}
