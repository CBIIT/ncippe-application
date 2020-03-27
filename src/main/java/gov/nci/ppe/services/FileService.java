package gov.nci.ppe.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;
import gov.nci.ppe.data.entity.Participant;
import gov.nci.ppe.data.entity.User;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-20
 */
@Component
public interface FileService {

	/**
	 * Insert a record into {FileMetadata} table
	 * @param S3Url - The location of the file in S3
	 * @param searchKey - Unique Key for the file uploaded used to retrieval
	 * @param fileName - Name of the file uploaded
	 * @param source - Source where the file was generated
	 * @param uploadedBy - User who uploaded the file
	 * @param patient - The participant for whom the file was uploaded
	 * @param fileType - Type of the file uploaded
	 */
	public void logFileMetadata(String S3Url, String searchKey, String fileName, String source, Long uploadedBy,
			Participant patient, Code fileType);
	
	/**
	 * Fetch the file details from {FileMetadata} table based on the file GUID
	 * @param fileGUID - Unique GUID for the file
	 * @return An optional object of FileMetadata
	 */
	public Optional<FileMetadata> getFileByFileGUID(String fileGUID);

	/**
	 * Update the Report's metadata to indicate that the given user has viewed the
	 * report
	 * 
	 * @param fileMetadata - the Report to mark as viewed
	 * @param user           - the User who viewed the report
	 * @return The updated record
	 */
	public FileMetadata markReportAsViewed(FileMetadata fileMetadata, User user);

	/**
	 * Fetch files uploaded during a given time period
	 * 
	 * @param startTime - start of the period
	 * @param endTime   - end of the period
	 * @return List of FileMetadata objects
	 */
	public List<FileMetadata> getFilesUploadedBetween(LocalDateTime startTime, LocalDateTime endTime);
}
