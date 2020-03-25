package gov.nci.ppe.data.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.FileMetadata;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

	Optional<FileMetadata> findByFileGUID(String fileGuid);

	List<FileMetadata> findByDateUploadedBetween(Timestamp startDate, Timestamp endDate);

}
