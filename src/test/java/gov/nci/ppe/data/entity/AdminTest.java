package gov.nci.ppe.data.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdminTest {

	
	@InjectMocks
	private Admin admin;
	
	private Long adminId;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		adminId = 10L;
	}
	
	@Test
	public void testSetterGetter() {
		admin.setAdminId(adminId);
		assertEquals(adminId, admin.getAdminId());
	}

}
