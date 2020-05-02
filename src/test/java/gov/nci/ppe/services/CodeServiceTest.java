package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.BaseMockitoTest;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.repository.CodeRepository;
import gov.nci.ppe.services.impl.CodeServiceImpl;

@ActiveProfiles("unittest")
@Tag("service")
@DisplayName("Unit Tests for CodeServiceImpl class")
public class CodeServiceTest implements BaseMockitoTest {

	@InjectMocks
	private CodeServiceImpl codeService;

	@Mock
	private CodeRepository mockCodeRepo;


	@Test
	public void testGetCode() {
		String input = "input";
		Code expected = new Code();
		expected.setCodeName(input);

		when(mockCodeRepo.findByCodeName(input)).thenReturn(expected);

		Code actual = codeService.getCode(input);

		assertNotNull(actual);

		assertEquals(expected, actual);
		verify(mockCodeRepo).findByCodeName(input);
	}
}
