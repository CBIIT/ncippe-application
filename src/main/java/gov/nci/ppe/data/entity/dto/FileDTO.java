package gov.nci.ppe.data.entity.dto;

import java.sql.Timestamp;
import java.util.Set;

/**
 * This class hides all the fields from FileMetadata object and display only
 * the required fields for JSON This class is populated using a Dozer object.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-06
 */
public class FileDTO {

	private String fileGUID;

	private String s3Url;

	private String fileType;

	private String fileName;

	private Timestamp dateUploaded;

	private Set<String> viewedBy;

	/**
	 * @return the fileGUID
	 */
	public String getFileGUID() {
		return fileGUID;
	}

	/**
	 * @param fileGUID the fileGUID to set
	 */
	public void setFileGUID(String fileGUID) {
		this.fileGUID = fileGUID;
	}

	/**
	 * @return the s3Url
	 */
	public String getS3Url() {
		return s3Url;
	}

	/**
	 * @param s3Url the s3Url to set
	 */
	public void setS3Url(String s3Url) {
		this.s3Url = s3Url;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the dateUploaded
	 */
	public Timestamp getDateUploaded() {
		return dateUploaded;
	}

	/**
	 * @param dateUploaded the dateUploaded to set
	 */
	public void setDateUploaded(Timestamp dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	/**
	 * @return the viewedBy
	 */
	public Set<String> getViewedBy() {
		return viewedBy;
	}

	/**
	 * @param viewedBy the viewedBy to set
	 */
	public void setViewedBy(Set<String> viewedBy) {
		this.viewedBy = viewedBy;
	}


}
