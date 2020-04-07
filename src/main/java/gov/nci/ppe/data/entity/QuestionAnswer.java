package gov.nci.ppe.data.entity;
/**
 * Class representing the table QuestionAnswer
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-09-29
 */

import java.time.LocalDateTime;

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

import lombok.Data;

@Data
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
	private LocalDateTime dateAnswered;

}
