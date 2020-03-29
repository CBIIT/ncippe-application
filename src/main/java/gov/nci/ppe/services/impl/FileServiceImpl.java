package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.FileMetadataRepository;
import gov.nci.ppe.services.FileService;

/**
 * This is a service class that handles all task related to file upload and marking files as viewed.
 * @author PublicisSapient
 * @version 1.0 
 * @since   2019-08-15
 */
@Service
public class FileServiceImpl implements FileService {

	@Autowired
	FileMetadataRepository fileMetadataRepo;

    /**
     * {@inheritDoc}
     */	
	@Override
	public void logFileMetadata(String S3Url, String searchKey, String fileName, String source, Long uploadedBy,
			Participant patient, Code fileType) {
		FileMetadata fileMetadata = new FileMetadata();
		fileMetadata.setS3Url(S3Url);
		fileMetadata.setFileType(fileType);
		fileMetadata.setFileName(fileName);
		fileMetadata.setSource(source);
		fileMetadata.setUploadedBy(uploadedBy);
		fileMetadata.setFileGUID(UUID.randomUUID().toString());
		fileMetadata.setParticipant(patient);
		fileMetadata.setDateUploaded(LocalDateTime.now());
		fileMetadata.setSearchKey(searchKey);
		fileMetadataRepo.save(fileMetadata);
	}

    /**
     * {@inheritDoc}
     */	
	@Override
	public Optional<FileMetadata> getFileByFileGUID(String fileGUID) {
		return fileMetadataRepo.findByFileGUID(fileGUID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileMetadata markReportAsViewed(FileMetadata fileMetadata, User user) {
		if (!fileMetadata.getViewedBy().contains(user)) {
			fileMetadata.getViewedBy().add(user);
		}
		return fileMetadataRepo.save(fileMetadata);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FileMetadata> getFilesUploadedBetween(LocalDateTime startTime, LocalDateTime endTime) {

		return fileMetadataRepo.findByDateUploadedBetween(startTime, endTime);
	}


}
