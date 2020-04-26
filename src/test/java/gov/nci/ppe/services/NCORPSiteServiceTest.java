package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.data.entity.NCORPSite;
import gov.nci.ppe.data.repository.NCORPSiteRepository;
import gov.nci.ppe.services.impl.NCORPSiteServiceImpl;

@ActiveProfiles("unittest")
@Tag("service")
@DisplayName("Unit Tests for NCORPSiteServiceImpl class")
public class NCORPSiteServiceTest {

	@InjectMocks
	private NCORPSiteServiceImpl ncorpSiteService;

	@Mock
	private NCORPSiteRepository mockSiteRepo;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetAllActiveSites() {
		List<NCORPSite> expected = new ArrayList<>();
		Mockito.when(mockSiteRepo.findByActiveTrue()).thenReturn(expected);

		List<NCORPSite> actual = ncorpSiteService.getAllActiveSites();
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());

		Mockito.verify(mockSiteRepo).findByActiveTrue();
	}
}
