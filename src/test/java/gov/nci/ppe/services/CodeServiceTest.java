package gov.nci.ppe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.services.impl.CodeServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class CodeServiceTest {

	@InjectMocks
	private CodeServiceImpl codeService;

	@Mock
	private CodeRepository mockCodeRepo;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetCode() {
		String input = "input";
		Code expected = new Code();
		expected.setCodeName(input);
		
		Mockito.when(mockCodeRepo.findByCodeName(input)).thenReturn(expected);

		Code actual = codeService.getCode(input);

		assertNotNull(actual);

		assertEquals(expected, actual);
		Mockito.verify(mockCodeRepo).findByCodeName(input);
	}
}
