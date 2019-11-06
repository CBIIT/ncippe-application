package gov.nci.ppe.data.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class BSSCTest {

	
	@InjectMocks
	private BSSC bssc;
	
	private Long bsscId;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		bsscId = 10L;
	}
	
	@Test
	public void testSetterGetter() {
		bssc.setBsscId(bsscId);
		assertEquals(bsscId, bssc.getBsscId());
	}
}
