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
    private static final String URL_USER = URL_API_VERSION + "/user";
    private static final String PARAM_USER_GUID = "/{userGUUID}";

    public static final String URL_NOTIFICATIONS = URL_API_VERSION + PARAM_NOTIFICATIONS;
    public static final String URL_HEALTHCHECK = "/healthcheck";
    public static final String URL_USER_NOTIFICATIONS = URL_USER + PARAM_USER_GUID + PARAM_NOTIFICATIONS;

    private UrlConstants() {

    }
}
