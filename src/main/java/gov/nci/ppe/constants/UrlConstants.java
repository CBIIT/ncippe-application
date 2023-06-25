package gov.nci.ppe.constants;

/**
 * Defines the URLs for the microservice
 * 
 * @author PublicisSapient
 * @version 2.4
 * @since 2021-05-17
 */
public class UrlConstants {

    private static final String PARAM_NOTIFICATIONS = "/notifications";
	private static final String URL_API_VERSION = "/api/v1";
	private static final String URL_PUBLIC_API_VERSION = "/publicapi/v1";
	private static final String URL_USER = URL_API_VERSION + "/user";

	private static final String PARAM_ALERTS = "/alerts";
	private static final String PARAM_USER_GUID = "/{userGUID}";
	public static final String REQ_PARAM_EMAIL = "email";
	public static final String REQ_PARAM_PATIENT_ID = "patientId";

	public static final String URL_NOTIFICATIONS = URL_API_VERSION + PARAM_NOTIFICATIONS;
	public static final String URL_HEALTHCHECK = "/healthcheck";
	public static final String URL_USER_NOTIFICATIONS = URL_USER + PARAM_USER_GUID + PARAM_NOTIFICATIONS;
	public static final String URL_NEWS_EVENTS = URL_PUBLIC_API_VERSION + "/newsEvents";
	public static final String URL_ALERTS = URL_PUBLIC_API_VERSION + PARAM_ALERTS;
	public static final String URL_USER_UPDATE_EMAIL = URL_USER + "/update-participant-email";

    // public static final String CHART_DATA = URL_PUBLIC_API_VERSION + "/chartData";
    public static final String CHART_DATA = "/chartData";
    private UrlConstants() {

	}
}
