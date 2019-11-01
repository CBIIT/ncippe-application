package gov.nci.ppe.constants;

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

}
