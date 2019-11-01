package gov.nci.ppe.data.entity.dto;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProviderDTOTest {

	@InjectMocks
	private ProviderDTO providerDTO;

	@Mock
	private ParticipantDTO mParticipantDTO;
	
	@Mock
	private CrcDTO mCrcDTO;

	private Set<ParticipantDTO> patients = new HashSet<>();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		patients.add(mParticipantDTO);
	}

	@Test
	public void testSetterGetterForParticipantDTO() {
		providerDTO.setPatients(patients);
		assertEquals(patients,providerDTO.getPatients());
	}
	
	@Test
	public void testSetterGetterForCrcDTO() {
		providerDTO.setCrcDto(mCrcDTO);
		assertEquals(mCrcDTO,providerDTO.getCrcDto());
	}


}
