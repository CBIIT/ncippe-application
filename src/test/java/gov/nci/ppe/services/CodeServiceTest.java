package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.services.impl.CodeServiceImpl;

@ActiveProfiles("unittest")
public class CodeServiceTest {

	@InjectMocks
	private CodeServiceImpl codeService;

	@Mock
	private CodeRepository mockCodeRepo;

	@BeforeEach
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
