package gov.nci.ppe.constants;

import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;

public class CommonConstants {

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String ERROR = "ERROR";


	public enum AuditEventType {
		PPE_LOGIN, PPE_ACCOUNT_CREATION, PPE_ACCOUNT_MODIFICATION, PPE_DEACTIVATE_ACCOUNT, PPE_WITHDRAW_FROM_PROGRAM,
		PPE_BIOMARKER_TEST_FILE_UPLOAD, PPE_ECONSENT_FILE_UPLOAD, PPE_INVITE_TO_PORTAL, PPE_INSERT_DATA_FROM_OPEN,
		PPE_UPDATE_DATA_FROM_OPEN
	}

	public enum ActionType {
		USER_DEACTIVATE_USER, USER_GET_USER, USER_UPDATE_USER, USER_INVITE_PARTICIPANT, USER_WITHDRAW_PARTICIPANT
	}

	public enum LanguageOption {
		ENGLISH("en"), SPANISH("es");

		private final String language;

		LanguageOption(String language) {
			this.language = language;
		}
		
		@JsonValue
		public String getLanguage() {
			return this.language;
		}
		
		public static LanguageOption getLanguageOption(String langStr) {
			if (StringUtils.isBlank(langStr)) {
				return null;
			}
			return Stream.of(LanguageOption.values()).filter(lang -> lang.getLanguage().equals(langStr)).findFirst()
					.orElseThrow(IllegalArgumentException::new);
		}

		public Locale getLocale() {
			return new Locale(this.language);
		}
	}
}
