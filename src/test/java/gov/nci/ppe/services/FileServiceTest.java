package gov.nci.ppe.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import gov.nci.ppe.constants.FileType;
import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.FileMetadataRepository;
import gov.nci.ppe.services.impl.FileServiceImpl;

@ActiveProfiles("unittest")
public class FileServiceTest {

	private static final String GUID = "aaaa";

	@InjectMocks
	private FileServiceImpl fileService;

	@Mock
	private FileMetadataRepository mockFileMetadataRepo;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetFileByFileGUID() {
		String uuid = "uuuu-uuuu-iiii-dddd";
		FileMetadata expected = new FileMetadata();
		expected.setFileGUID(uuid);
		Optional<FileMetadata> expectedOpt = Optional.of(expected);

		Mockito.when(mockFileMetadataRepo.findByFileGUID(uuid)).thenReturn(expectedOpt);

		Optional<FileMetadata> resultOpt = fileService.getFileByFileGUID(uuid);

		assertEquals(expected, resultOpt.get());
		Mockito.verify(mockFileMetadataRepo).findByFileGUID(uuid);
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
		assertAll(() -> assertEquals(S3Url, savedFM.getS3Url()), () -> assertEquals(searchKey, savedFM.getSearchKey()),
				() -> assertEquals(fileName, savedFM.getFileName()), () -> assertEquals(source, savedFM.getSource()),
				() -> assertEquals(uploadedBy, savedFM.getUploadedBy()),
				() -> assertEquals(patient, savedFM.getParticipant()),
				() -> assertEquals(fileType, savedFM.getFileType()), () -> assertNotNull(savedFM.getFileGUID()),
				() -> assertNotNull(savedFM.getDateUploaded()));
	}

	@Test
	public void testGetFilesUploadedBetween() {
		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime endTime = startTime.plusDays(2);
		Code eConsentType = new Code();
		eConsentType.setCodeId(-1l);
		eConsentType.setCodeName(FileType.PPE_FILETYPE_ECONSENT_FORM.getFileType());
		List<FileMetadata> expectedFiles = new ArrayList<>();
		FileMetadata fm = new FileMetadata();
		fm.setFileGUID(GUID);
		fm.setFileType(eConsentType);
		expectedFiles.add(fm);

		when(mockFileMetadataRepo.findByFileTypeAndDateUploadedBetween(eConsentType, startTime, endTime))
				.thenReturn(expectedFiles);

		List<FileMetadata> results = fileService.getFilesUploadedBetween(eConsentType, startTime, endTime);
		verify(mockFileMetadataRepo).findByFileTypeAndDateUploadedBetween(eConsentType, startTime, endTime);

		assertFalse(results.isEmpty());
		assertEquals(GUID, results.get(0).getFileGUID());
	}
}
