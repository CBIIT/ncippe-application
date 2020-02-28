package gov.nci.ppe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.FileMetadataRepository;
import gov.nci.ppe.services.impl.FileServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
public class FileServiceTest {

	@InjectMocks
	private FileServiceImpl fileService;

	@Mock
	private FileMetadataRepository mockFileMetadataRepo;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFileByFileGUID() {
		String uuid = "uuuu-uuuu-iiii-dddd";
		FileMetadata expected =   new FileMetadata();
		expected.setFileGUID(uuid);
		Optional<FileMetadata> expectedOpt = Optional.of(expected);

		Mockito.when(mockFileMetadataRepo.findFileByGUID(uuid)).thenReturn(expectedOpt);

		Optional<FileMetadata> resultOpt = fileService.getFileByFileGUID(uuid);

		assertEquals(expected, resultOpt.get());
		Mockito.verify(mockFileMetadataRepo).findFileByGUID(uuid);
	}

	@Test
	public void testMarkReportAsViewed() {
		FileMetadata fileMetadata = new FileMetadata();
		User user = new User();
		user.setUserId(1L);
		Mockito.when(mockFileMetadataRepo.save(fileMetadata)).thenReturn(fileMetadata);


		FileMetadata result = fileService.markReportAsViewed(fileMetadata, user);

		assertNotNull(result);
		Mockito.verify(mockFileMetadataRepo).save(fileMetadata);
		assertTrue(result.getViewedBy().contains(user));
	}

	@Test
	public void testLogFileMetadata() {
		ArgumentCaptor<FileMetadata> arg = ArgumentCaptor.forClass(FileMetadata.class);

		String S3Url = "S3 URL";
		String searchKey = "search key";
		String fileName = "file name";
		String source = "source";
		Long uploadedBy = 1L;
		Participant patient = new Participant();
		patient.setUserId(2L);
		Code fileType = new Code();
		fileType.setCodeId(1L);
		fileType.setCodeName("File Type Report");
		fileService.logFileMetadata(S3Url, searchKey, fileName, source, uploadedBy, patient, fileType);

		Mockito.verify(mockFileMetadataRepo).save(arg.capture());
		FileMetadata savedFM = arg.getValue();
		assertEquals(S3Url, savedFM.getS3Url());
		assertEquals(searchKey, savedFM.getSearchKey());
		assertEquals(fileName, savedFM.getFileName());
		assertEquals(source, savedFM.getSource());
		assertEquals(uploadedBy, savedFM.getUploadedBy());
		assertEquals(patient, savedFM.getParticipant());
		assertEquals(fileType, savedFM.getFileType());
		assertNotNull(savedFM.getFileGUID());
		assertNotNull(savedFM.getDateUploaded());
	}
}
