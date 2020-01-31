package gov.nci.ppe.constants;

public class CommonConstants {

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String ERROR = "ERROR";

	public static final String APPLICATION_CONTENTTYPE_JSON = "application/json";
	public static final String APPLICATION_CONTENTTYPE_PDF = "application/pdf";

	public enum AuditEventType {
		PPE_LOGIN, PPE_ACCOUNT_CREATION, PPE_ACCOUNT_MODIFICATION, PPE_DEACTIVATE_ACCOUNT, PPE_WITHDRAW_FROM_PROGRAM,
		PPE_BIOMARKER_TEST_FILE_UPLOAD, PPE_ECONSENT_FILE_UPLOAD, PPE_INVITE_TO_PORTAL, PPE_INSERT_DATA_FROM_OPEN,
		PPE_UPDATE_DATA_FROM_OPEN
	}

	public enum ActionType {
		USER_DEACTIVATE_USER, USER_GET_USER, USER_UPDATE_USER, USER_INVITE_PARTICIPANT, USER_WITHDRAW_PARTICIPANT
	}
}
