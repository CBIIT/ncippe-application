package gov.nci.ppe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nci.ppe.data.entity.NCORPSite;
import gov.nci.ppe.data.repository.NCORPSiteRepository;
import gov.nci.ppe.services.impl.NCORPSiteServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class NCORPSiteServiceTest {

	@InjectMocks
	private NCORPSiteServiceImpl ncorpSiteService;

	@Mock
	private NCORPSiteRepository mockSiteRepo;

	@Before
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
