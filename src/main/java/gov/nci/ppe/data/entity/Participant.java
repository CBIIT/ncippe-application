package gov.nci.ppe.data.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-29
 */
@Entity
@DiscriminatorValue("1")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Participant extends User {

	@Column(name = "UserId")
	private Long participantId;

	@EqualsAndHashCode.Include
	@Column(name = "PatientID", nullable = false, length = 20)
	private String patientId;



	@ManyToMany
	@JoinTable(name = "ProviderParticipant", joinColumns = {
			@JoinColumn(name = "ParticipantId") }, inverseJoinColumns = { @JoinColumn(name = "ProviderId") })
	private Set<Provider> providers = new HashSet<>(); // Make CRC like this

	@OneToMany(mappedBy = "participant")
	@Where(clause = "FileType = 102")
	@OrderBy("DateUploaded DESC")
	private List<FileMetadata> otherDocuments = new ArrayList<>();

	@OneToMany(mappedBy = "participant")
	@Where(clause = "FileType = 101")
	@OrderBy("DateUploaded DESC")
	List<FileMetadata> reports;

	@OneToOne
	// @ManyToMany
	@JoinTable(name = "CRCParticipant", joinColumns = @JoinColumn(name = "ParticipantId"), inverseJoinColumns = @JoinColumn(name = "CRCId"))
	private CRC crc;
	// private Set<CRC> crc = new HashSet<>(); // MHL @CHECKME



	@Transient
	private boolean hasNewReports;

	@Column(name = "IsActiveBiobankParticipant", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean isActiveBiobankParticipant;

	@OneToMany(mappedBy = "participantForQA")
	@OrderBy("DateAnswered DESC, QuestionOrder ASC")
	private List<QuestionAnswer> questionAnswers;

	@Column(name = "PatientDOB")
	private LocalDate dateOfBirth;

	@Override
	public String toString() {
		StringBuilder retValue = new StringBuilder("{");
		retValue.append(StringUtils.CR).append(" Participant Id : ").append(participantId).append(",")
				.append(StringUtils.CR).append(" Name : ").append(this.getFirstName()).append(" ")
				.append(this.getLastName()).append(StringUtils.CR).append("Phone : ").append(this.getPhoneNumber())
				.append(",").append(StringUtils.CR).append("Email Id : ").append(this.getEmail()).append(StringUtils.CR)
				.append("}");
		return retValue.toString();
	}






}
