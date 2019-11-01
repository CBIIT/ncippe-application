package gov.nci.ppe.data.entity.dto;

/**
 * Data transfer Object representing QuestionAnswer class
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-29
 */

import org.apache.commons.lang3.StringUtils;

public class QuestionAnswerDTO {
	
	private String questionOrder;
	private String question;
	private String answer;
	/**
	 * @return the orderNumber
	 */
	public String getQuestionOrder() {
		return questionOrder;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setQuestionOrder(String questionOrder) {
		this.questionOrder = questionOrder;
	}
	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String toString() {
		StringBuilder retValue = new StringBuilder("{");
		return retValue.append(StringUtils.CR).append("orderNumber = ").append(questionOrder)
				.append(StringUtils.CR).append("question = ").append(question)
				.append(StringUtils.CR).append("answer = ").append(answer)
				.append(StringUtils.CR).append("}").toString();
		
	}

}
