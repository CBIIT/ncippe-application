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
public class CrcDTOTest {
	
	@InjectMocks
	private CrcDTO crcDTO;
	
	private Long crcId;

	@Mock
	private ParticipantDTO mParticipantDTO;
	
	private Set<ParticipantDTO> patients = new HashSet<>();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		patients.add(mParticipantDTO);
		crcId = 10L;
	}

	@Test
	public void testSetterGetterForParticipantDTO() {
		crcDTO.setPatients(patients);
		assertEquals(patients,crcDTO.getPatients());
	}
	
	@Test
	public void testSetterGetterForCrcId() {
		crcDTO.setCrcId(crcId);
		assertEquals(crcId, crcDTO.getCrcId());
	}

}
