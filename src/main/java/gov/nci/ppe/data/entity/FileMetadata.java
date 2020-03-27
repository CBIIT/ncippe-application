package gov.nci.ppe.data.entity;

import java.time.LocalDateTime;
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

import lombok.Data;

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
@Data
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
	private LocalDateTime dateUploaded;

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

	/**
	 * Checks if the user with the specified UUID has viewed the File
	 * 
	 * @param userUUID - UUID of the User to check for
	 * @return true, if the User has viewed the file, false otherwise
	 */
	public boolean hasViewed(String userUUID) {
		return viewedBy.stream().anyMatch(user -> user.getUserUUID().equals(userUUID));
	}

}
