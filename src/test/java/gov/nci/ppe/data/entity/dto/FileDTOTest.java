package gov.nci.ppe.data.entity.dto;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileDTOTest {
	@InjectMocks
	private FileDTO fileDTO;
	
	private String reportGUID;
	private String s3Url;
	private String fileType;
	private String reportName;
	private Timestamp dateUploaded;
	private Set<String> viewedBy = new HashSet<>();
	private String viewedByUser;
	
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reportGUID = "Rpt100";
		s3Url = "www.google.com";
		fileType = "TestReports";
		reportName = "DummyPatient_Report";
		dateUploaded = new Timestamp(System.currentTimeMillis());
		viewedByUser = "DummyUser";
		viewedBy.add(viewedByUser);
	}
	
	@Test
	public void testSetterGetter() {
		fileDTO.setFileGUID(reportGUID);
		fileDTO.setS3Url(s3Url);
		fileDTO.setFileType(fileType);
		fileDTO.setFileName(reportName);
		fileDTO.setDateUploaded(dateUploaded);
		fileDTO.setViewedBy(viewedBy);
		assertEquals(reportGUID , fileDTO.getFileGUID());
		assertEquals(s3Url , fileDTO.getS3Url());
		assertEquals(fileType , fileDTO.getFileType());
		assertEquals(reportName , fileDTO.getFileName());
		assertEquals(dateUploaded , fileDTO.getDateUploaded());
		assertEquals(viewedBy , fileDTO.getViewedBy());
	}

}
