package gov.nci.ppe.constants;

/**
 * This is a constant class representing ENUMs of variable database constants.
 * 
 * @author PublicisSapient
 * @version 1.0
 * @since 2019-08-22
 */
public class DatabaseConstants {

	public enum PortalAccountStatus {
		ACCT_NEW, ACCT_INITIATED, ACCT_ACTIVE, ACCT_TERMINATED_AT_PPE, ACCT_TERMINATED_AT_LOGIN_GOV;
	}

	public enum QuestionAnswerType {
		PPE_WITHDRAW_SURVEY_QUESTION("PPE_WITHDRAW_SURVEY_QUESTION");

		private String questionAnswerType;

		QuestionAnswerType(String questionAnswerType) {
			this.questionAnswerType = questionAnswerType;
		}

		public String getQuestionAnswerType() {
			return questionAnswerType;
		}
	}
	
	/* Represents the UserType mentioned in User Table */
	public enum UserType{
		PPE_PARTICIPANT, PPE_PROVIDER, PPE_ADMIN, PPE_CRC, PPE_BSSC, PPE_MOCHA_ADMIN;
	}

}
