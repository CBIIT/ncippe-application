package gov.nci.ppe.data.entity;

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

/**
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-07-29
 */
@Entity
@DiscriminatorValue("1")
public class Participant extends User {

	@Column(name = "UserId")
	private Long participantId;

	@Column(name = "PatientID", nullable = false, length = 32)
	private String patientId;

	@ManyToMany
	@JoinTable(name = "ProviderParticipant", joinColumns = {
			@JoinColumn(name = "ParticipantId") }, inverseJoinColumns = { @JoinColumn(name = "ProviderId") })
	private Set<Provider> providers = new HashSet<>();

	@OneToMany(mappedBy = "participant")
	@Where(clause = "FileType = 102")
	@OrderBy("DateUploaded DESC")
	private List<FileMetadata> otherDocuments = new ArrayList<>();

	@OneToMany(mappedBy = "participant")
	@Where(clause = "FileType = 101")
	@OrderBy("DateUploaded DESC")
	List<FileMetadata> reports;

	@OneToOne
	@JoinTable(name = "CRCParticipant", joinColumns = @JoinColumn(name = "ParticipantId"), inverseJoinColumns = @JoinColumn(name = "CRCId"))
	private CRC crc;

	@Transient
	private boolean hasNewReports;

	@Column(name = "IsActiveBiobankParticipant", nullable = false, columnDefinition = "TINYINT(1)")
	private boolean isActiveBiobankParticipant;

	@OneToMany(mappedBy = "participantForQA")
	@OrderBy("DateAnswered DESC, QuestionOrder ASC")
	List<QuestionAnswer> questionAnswers;

	public Long getParticipantId() {
		return participantId;
	}

	public void setParticipantId(Long participantId) {
		this.participantId = participantId;
	}

	public Set<Provider> getProviders() {
		return providers;
	}

	public void setProviders(Set<Provider> providers) {
		this.providers = providers;
	}

	public List<FileMetadata> getReports() {
		return reports;
	}

	public void setReports(List<FileMetadata> reports) {
		this.reports = reports;
	}

	public CRC getCRC() {
		return crc;
	}

	public void setCRC(CRC crc) {
		this.crc = crc;
	}

	public boolean isHasNewReports() {
		return hasNewReports;
	}

	public void setHasNewReports(boolean hasNewReports) {
		this.hasNewReports = hasNewReports;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientID) {
		this.patientId = patientID;
	}

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

	/**
	 * @return the otherDocuments
	 */
	public List<FileMetadata> getOtherDocuments() {
		return otherDocuments;
	}

	/**
	 * @param otherDocuments the otherDocuments to set
	 */
	public void setOtherDocuments(List<FileMetadata> otherDocuments) {
		this.otherDocuments = otherDocuments;
	}

	/**
	 * @return the isActiveBiobankParticipant
	 */
	public boolean getIsActiveBiobankParticipant() {
		return isActiveBiobankParticipant;
	}

	/**
	 * @param isActiveBiobankParticipant the isActiveBiobankParticipant to set
	 */
	public void setIsActiveBiobankParticipant(boolean isActiveBiobankParticipant) {
		this.isActiveBiobankParticipant = isActiveBiobankParticipant;
	}

	/**
	 * @return the questionAnswers
	 */
	public List<QuestionAnswer> getQuestionAnswers() {
		return questionAnswers;
	}

	/**
	 * @param questionAnswers the questionAnswers to set
	 */
	public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}

}
