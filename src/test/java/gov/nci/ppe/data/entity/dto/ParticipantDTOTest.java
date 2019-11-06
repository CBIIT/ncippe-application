package gov.nci.ppe.data.entity.dto;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParticipantDTOTest {
	
	@InjectMocks
	private ParticipantDTO participantDTO;
	
	@Mock
	private ProviderDTO mProviderDTO;
	
	@Mock
	private FileDTO mFileDTO;
	
	@Mock
	private CrcDTO mCRCDTO;
	
	private Set<ProviderDTO> providerList = null;
	private List<FileDTO> reports = null;
	private boolean hasNewReports;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		providerList = new HashSet<>();
		providerList.add(mProviderDTO);
		reports = new ArrayList<>();
		reports.add(mFileDTO);
		hasNewReports = true;
	}
	
	@Test
	public void testSetterGetterForCrcDTO() {
		participantDTO.setCrc(mCRCDTO);
		assertEquals(mCRCDTO, participantDTO.getCrc());
	}
	
	@Test
	public void testSetterGetterForProviderDTOList() {
		participantDTO.setProviders(providerList);
		assertEquals(providerList, participantDTO.getProviders());
		assertEquals(true, participantDTO.getProviders().contains(mProviderDTO));
	}
	
	@Test
	public void testSetterGetterForFileDTO() {
		participantDTO.setReports(reports);
		assertEquals(reports, participantDTO.getReports());
	}
	
	@Test
	public void testSetterGetterForNewReportFlag() {
		participantDTO.setHasNewReports(hasNewReports);
		assertEquals(true, participantDTO.isHasNewReports());
	}

}
