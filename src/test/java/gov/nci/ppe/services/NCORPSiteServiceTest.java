package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
		when(mockSiteRepo.findByActiveTrue()).thenReturn(expected);

		List<NCORPSite> actual = ncorpSiteService.getAllActiveSites();
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());

		verify(mockSiteRepo).findByActiveTrue();
		verifyNoMoreInteractions(mockSiteRepo);
	}

	@Test
	public void testGetSite_Found() {
		NCORPSite expected = new NCORPSite();
		final String ctepId = UUID.randomUUID().toString();
		expected.setCTEPId(ctepId);
		when(mockSiteRepo.findByCTEPId(ctepId)).thenReturn(Optional.of(expected));

		NCORPSite actual = ncorpSiteService.getSite(ctepId);

		assertNotNull(actual);
		assertEquals(actual.getCTEPId(), ctepId);
		verify(mockSiteRepo).findByCTEPId(ctepId);
		verifyNoMoreInteractions(mockSiteRepo);
	}

	@Test
	public void testGetSite_NotFound() {
		final String ctepId = UUID.randomUUID().toString();
		when(mockSiteRepo.findByCTEPId(ctepId)).thenReturn(Optional.empty());

		NCORPSite actual = ncorpSiteService.getSite(ctepId);

		assertNull(actual);
		verify(mockSiteRepo).findByCTEPId(ctepId);
		verifyNoMoreInteractions(mockSiteRepo);

	}
}
