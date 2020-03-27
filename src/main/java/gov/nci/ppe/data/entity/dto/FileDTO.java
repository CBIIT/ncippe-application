package gov.nci.ppe.data.entity.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

/**
 * This class hides all the fields from FileMetadata object and display only
 * the required fields for JSON This class is populated using a Dozer object.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-06
 */
@Data
public class FileDTO {

	private String fileGUID;

	private String s3Url;

	private String fileType;

	private String fileName;

	private LocalDateTime dateUploaded;

	private Set<String> viewedBy;

}
