package gov.nci.ppe.data.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.Code;
import gov.nci.ppe.data.entity.FileMetadata;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

	/**
	 * Find FileMetadata by the GUID
	 * 
	 * @param fileGuid - Primary Key Identifier of the FileMetadat object
	 * @return The FileMetadata object, if found
	 */
	Optional<FileMetadata> findByFileGUID(String fileGuid);

	/**
	 * Return list of FileMetadata objects of the specified type uploaded during the
	 * specified period
	 * 
	 * @param fileType  - Type of file
	 * @param startTime - Beginning of period
	 * @param endTime   - End of period
	 * @return - List of FileMetadata objects with DateUploaded field falling
	 *         between startDate and endDate
	 */
	List<FileMetadata> findByFileTypeandDateUploadedBetween(Code fileType, LocalDateTime startTime,
			LocalDateTime endTime);

}
