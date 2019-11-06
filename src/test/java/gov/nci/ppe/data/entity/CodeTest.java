package gov.nci.ppe.data.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CodeTest {

	@InjectMocks
	private Code code;

	@Mock
	private CodeCategory mCodeCategory;

	private Long codeId;
	private String codeName;
	private String description;
	private int isActive;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		codeId = 11L;
		codeName = "Admin";
		description = "Admin Test";
		isActive = 1;
	}

	@Test
	public void testSetterGetter() {
		code.setCodeId(codeId);
		code.setCodeCategory(mCodeCategory);
		code.setCodeName(codeName);
		code.setDescription(description);
		code.setIsActive(isActive);
		assertEquals(codeId, code.getCodeId());
		assertEquals(mCodeCategory, code.getCodeCategory());
		assertEquals(codeName, code.getCodeName());
		assertEquals(description, code.getDescription());
		assertEquals(isActive, code.getIsActive());
	}

}
