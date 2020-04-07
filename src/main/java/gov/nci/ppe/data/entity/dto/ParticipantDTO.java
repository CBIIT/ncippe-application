package gov.nci.ppe.data.entity.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import gov.nci.ppe.data.entity.FileMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class hides all the fields from Participant object and display only the
 * required fields for JSON This class is populated using a Dozer object. *
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ParticipantDTO extends UserDTO {

	@EqualsAndHashCode.Include
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

	private String dateOfBirth;


	@Override
	public String toString() {
		StringBuilder retValue = new StringBuilder("{");
		retValue.append(super.toString()).append(StringUtils.CR).append(" crc : ").append(crc.toString())
				.append(StringUtils.CR).append("}");
		return retValue.toString();
	}

}
