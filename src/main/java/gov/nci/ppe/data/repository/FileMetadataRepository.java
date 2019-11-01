package gov.nci.ppe.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.nci.ppe.data.entity.FileMetadata;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

	@Query(value = "SELECT * FROM FileMetadata RPT WHERE RPT.FileGUID = :fileGuid",nativeQuery=true)
	Optional<FileMetadata> findFileByGUID(@Param("fileGuid") String fileGuid);
}
