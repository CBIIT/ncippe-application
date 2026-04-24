package gov.nci.ppe.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class LogoutController {

    private static final Logger logger = Logger.getLogger(LogoutController.class.getName());

    @Value("${spring.security.oauth2.client.registration.logingov.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.logingov.client-secret}")
    private String clientSecret;

    @Value("${logingov.logout-url}")
    private String logoutUrl; // https://stsstg.nih.gov/connect/session/logout

    @Value("${logingov.logout-redirect-uri}")
    private String postLogoutRedirectUri; // https://moonshotbiobank-dev.cancer.gov/signout

    @Value("${logingov.smsession-url}")
    private String postLogoutSMSessionUrl;

    private final RestTemplate restTemplate ;
    public LogoutController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/logout-sts")
    public ResponseEntity<Void> logoutSTS(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String idToken = body.get("id_token");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // --- Back-Channel Logout to OIDC Provider (secure, server-side) ---
        HttpHeaders oidcHeaders = new HttpHeaders();
        oidcHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        oidcHeaders.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> oidcForm = new LinkedMultiValueMap<>();
        if (idToken != null) {
            // The id_token_hint is crucial for the OIDC provider to know which session to terminate.
            oidcForm.add("id_token", idToken);
        }
        HttpEntity<MultiValueMap<String, String>> oidcRequest = new HttpEntity<>(oidcForm, oidcHeaders);

        try {
            restTemplate.exchange(logoutUrl, HttpMethod.POST, oidcRequest, String.class);
            logger.info("Back-channel OIDC logout call successful.");
        } catch (Exception e) {
            logger.warning("Error during back-channel OIDC logout: " + e.getMessage());
        }

        // --- Front-Channel Logout to SiteMinder (triggers browser redirect) ---
        String ssoLogoutTarget = UriComponentsBuilder.fromUriString(postLogoutSMSessionUrl)
                .queryParam("target", postLogoutRedirectUri)
                .build()
                .toUriString();

        HttpHeaders redirectHeaders = new HttpHeaders();
        redirectHeaders.setLocation(URI.create(ssoLogoutTarget));

        return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);
    }
}

