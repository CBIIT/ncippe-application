package gov.nci.ppe.data.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Represents the
 * <table>
 * FileMetadata
 * </table>
 * in the database
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-15
 */
@Entity
@Table(name = "FileMetadata")
public class FileMetadata {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fileMetadataId;

	@Column(name = "FileGUID", nullable = false, length = 45)
	private String fileGUID;

	@Column(name = "S3URL", nullable = false, length = 255)
	private String s3Url;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FileType")
	@Fetch(FetchMode.JOIN)
	private Code fileType;

	@Column(name = "FileName", nullable = false, length = 255)
	private String fileName;

	@Column(name = "DateUploaded", nullable = false)
	private Timestamp dateUploaded;

	@Column(name = "Source", nullable = false, length = 45)
	private String source;

	@Column(name = "UploadedBy", nullable = false, length = 11)
	private Long uploadedBy;

	@ManyToOne(targetEntity = Participant.class)
	@JoinColumn(name = "PatientId")
	private Participant participant;

	@Column(name = "SearchKey", length = 255)
	private String searchKey;

	@ManyToMany
	@JoinTable(name = "FileMetaDataViewedBy", joinColumns = {
			@JoinColumn(name = "FileMetaDataId") }, inverseJoinColumns = { @JoinColumn(name = "ViewedByUserId") })
	private Set<User> viewedBy = new HashSet<>();

	public Long getFileMetadataId() {
		return fileMetadataId;
	}

	public void setFileMetadataId(Long fileMetadataId) {
		this.fileMetadataId = fileMetadataId;
	}

	public String getFileGUID() {
		return fileGUID;
	}

	public void setFileGUID(String fileGUID) {
		this.fileGUID = fileGUID;
	}

	public String getS3Url() {
		return s3Url;
	}

	public void setS3Url(String s3Url) {
		this.s3Url = s3Url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Timestamp getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(Timestamp dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Long getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(Long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public Participant getParticipant() {
		return participant;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Set<User> getViewedBy() {
		return viewedBy;
	}

	public void setViewedBy(Set<User> viewedBy) {
		this.viewedBy = viewedBy;
	}

	/**
	 * @return the fileType
	 */
	public Code getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(Code fileType) {
		this.fileType = fileType;
	}

}
