package gov.nci.ppe.data.entity.dto;

/**
 * Data transfer Object representing QuestionAnswer class
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-29
 */

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
	
	private String questionOrder;
	private String question;
	private String answer;
	
	@Override
	public String toString() {
		StringBuilder retValue = new StringBuilder("{");
		return retValue.append(StringUtils.CR).append("orderNumber = ").append(questionOrder)
				.append(StringUtils.CR).append("question = ").append(question)
				.append(StringUtils.CR).append("answer = ").append(answer)
				.append(StringUtils.CR).append("}").toString();
		
	}

}
