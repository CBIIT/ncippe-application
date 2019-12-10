package gov.nci.ppe.data.entity;
/**
 * Class representing the table QuestionAnswer
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-29
 */

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "QuestionAnswer")
public class QuestionAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionAnswerId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QuestionCategoryCodeId")
	@Fetch(FetchMode.JOIN)
	private Code questionCategory;

	@Column(name = "QuestionOrder", nullable = false, length = 11)
	private Long questionOrder;

	@Column(name = "Question", nullable = false, length = 512)
	private String question;

	@Column(name = "Answer", nullable = true, length = 512, columnDefinition = "default 'No Answer'")
	private String answer;

	@ManyToOne(targetEntity = Participant.class)
	@JoinColumn(name = "ParticipantId")
	private Participant participantForQA;

	@Column(name = "DateAnswered", nullable = false)
	private Timestamp dateAnswered;

	/**
	 * @return the questionAnswerId
	 */
	public Long getQuestionAnswerId() {
		return questionAnswerId;
	}

	/**
	 * @param QuestionAnswerId the QuestionAnswerId to set
	 */
	public void setQuestionAnswerId(Long questionAnswerId) {
		this.questionAnswerId = questionAnswerId;
	}

	/**
	 * @return the questionCategory
	 */
	public Code getQuestionCategory() {
		return questionCategory;
	}

	/**
	 * @param questionCategory the questionCategory to set
	 */
	public void setQuestionCategory(Code questionCategory) {
		this.questionCategory = questionCategory;
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

	/**
	 * @return the participantForQA
	 */
	public Participant getParticipantForQA() {
		return participantForQA;
	}

	/**
	 * @param participantForQA the participantForQA to set
	 */
	public void setParticipantForQA(Participant participantForQA) {
		this.participantForQA = participantForQA;
	}

	/**
	 * @return the dateAnswered
	 */
	public Timestamp getDateAnswered() {
		return dateAnswered;
	}

	/**
	 * @param dateAnswered the dateAnswered to set
	 */
	public void setDateAnswered(Timestamp dateAnswered) {
		this.dateAnswered = dateAnswered;
	}

	/**
	 * @return the questionOrder
	 */
	public Long getQuestionOrder() {
		return questionOrder;
	}

	/**
	 * @param questionOrder the questionOrder to set
	 */
	public void setQuestionOrder(Long questionOrder) {
		this.questionOrder = questionOrder;
	}
}
