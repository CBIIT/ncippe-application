package gov.nci.ppe.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;
import gov.nci.ppe.data.repository.FileMetadataRepository;
import gov.nci.ppe.data.repository.UserRepository;
import gov.nci.ppe.services.FileService;

/**
 * This is a service class that handles all task related to file upload and
 * marking files as viewed.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */
@Service
public class FileServiceImpl implements FileService {

	@Autowired
	FileMetadataRepository fileMetadataRepo;

	@Autowired
	UserRepository userRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logFileMetadata(String s3Url, String searchKey, String fileName, String source, Long uploadedBy,
			Participant patient, Code fileType) {
		FileMetadata fileMetadata = new FileMetadata();
		fileMetadata.setS3Url(s3Url);
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
	@Transactional(readOnly = true)
	public Optional<FileMetadata> getFileByFileGUID(String fileGUID) {
		return fileMetadataRepo.findByFileGUID(fileGUID).map(fm -> {
			if (fm.getFileType() != null) {
				Hibernate.initialize(fm.getFileType());
			}
			if (fm.getParticipant() != null) {
				Hibernate.initialize(fm.getParticipant());
			}
			return fm;
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public FileMetadata markReportAsViewed(FileMetadata fileMetadata, User user) {
		FileMetadata managed = fileMetadataRepo.findById(fileMetadata.getFileMetadataId())
				.orElseThrow(() -> new IllegalArgumentException("FileMetadata not found"));
		User managedViewer = userRepository.findById(user.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		if (!managed.getViewedBy().contains(managedViewer)) {
			managed.getViewedBy().add(managedViewer);
		}
		FileMetadata saved = fileMetadataRepo.save(managed);
		if (saved.getFileType() != null) {
			Hibernate.initialize(saved.getFileType());
		}
		if (saved.getParticipant() != null) {
			Hibernate.initialize(saved.getParticipant());
		}
		Hibernate.initialize(saved.getViewedBy());
		return saved;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FileMetadata> getFilesUploadedBetween(Code fileType, LocalDateTime startTime, LocalDateTime endTime) {

		return fileMetadataRepo.findByFileTypeAndDateUploadedBetween(fileType, startTime, endTime);
	}

}
