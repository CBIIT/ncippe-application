package gov.nci.ppe.data.entity.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import gov.nci.ppe.data.entity.FileMetadata;

/**
 * This class hides all the fields from Participant object and display only the
 * required fields for JSON This class is populated using a Dozer object. *
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-26
 */
public class ParticipantDTO extends UserDTO {

	private String patientId;

	@JsonView(JsonViews.ParticipantDetailView.class)
	private CrcDTO crc;

	@JsonView(JsonViews.ParticipantDetailView.class)
	private Set<ProviderDTO> providers = new HashSet<>();

	@JsonView(JsonViews.ParticipantDetailView.class)
	private List<FileDTO> reports = null;

	@JsonView({ JsonViews.CrcDetailView.class, JsonViews.ProviderDetailView.class })
	private boolean hasNewReports;

	@JsonView(JsonViews.ParticipantDetailView.class)
	private List<FileMetadata> otherDocuments = null;

	private boolean isActiveBiobankParticipant;

	@JsonView(JsonViews.ParticipantDetailView.class)
	private List<QuestionAnswerDTO> questionAnswers = null;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientID) {
		this.patientId = patientID;
	}

	public CrcDTO getCrc() {
		return crc;
	}

	public void setCrc(CrcDTO crc) {
		this.crc = crc;
	}

	@Override
	public String toString() {
		StringBuilder retValue = new StringBuilder("{");
		retValue.append(super.toString()).append(StringUtils.CR).append(" crc : ").append(crc.toString())
				.append(StringUtils.CR).append("}");
		return retValue.toString();
	}

	public Set<ProviderDTO> getProviders() {
		return providers;
	}

	public void setProviders(Set<ProviderDTO> providers) {
		this.providers = providers;
	}

	public List<FileDTO> getReports() {
		return reports;
	}

	public void setReports(List<FileDTO> reports) {
		this.reports = reports;
	}

	public boolean isHasNewReports() {
		return hasNewReports;
	}

	public void setHasNewReports(boolean hasNewReports) {
		this.hasNewReports = hasNewReports;
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
	public List<QuestionAnswerDTO> getQuestionAnswers() {
		return questionAnswers;
	}

	/**
	 * @param questionAnswers the questionAnswers to set
	 */
	public void setQuestionAnswers(List<QuestionAnswerDTO> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}
}
