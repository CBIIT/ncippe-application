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
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LogoutController {

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
        // --- Debugging Incoming Request Headers ---
        System.out.println("--- Incoming Request Headers for /api/logout/full ---");
        // retrieve the id_token from the request body
        String idToken = body.get("id_token");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        System.out.println("------------------------------------------------------");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // --- Back-Channel Logout to OIDC Provider (secure, server-side) ---
        HttpHeaders oidcHeaders = new HttpHeaders();
        oidcHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        System.out.println("credentials: " + credentials);
        //oidcHeaders.setBasicAuth(credentials);
        oidcHeaders.setBasicAuth(clientId, clientSecret);

//        Enumeration<String> oidcheaderNames = (Enumeration<String>) oidcHeaders;
//        while (oidcheaderNames.hasMoreElements()) {
//            String oidcheaderName = oidcheaderNames.nextElement();
//            System.out.println(oidcheaderName );
//        }
//        System.out.println("------------------------------------------------------");

        MultiValueMap<String, String> oidcForm = new LinkedMultiValueMap<>();
        if (idToken != null) {
            // The id_token_hint is crucial for the OIDC provider to know which session to terminate.
            oidcForm.add("id_token", idToken);
        }
        HttpEntity<MultiValueMap<String, String>> oidcRequest = new HttpEntity<>(oidcForm, oidcHeaders);

        try {
            // This is the explicit call to the oidcEndSessionEndpoint
            restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    oidcRequest,
                    String.class
            );
            System.out.println("Back-channel OIDC logout call successful.");
        } catch (Exception e) {
            System.err.println("Error during back-channel OIDC logout: " + e.getMessage());
            // Log the error but continue with the front-channel logout
        }

        // --- Placeholder for making the back-channel POST call ---
        // You would use RestTemplate or WebClient here to make the call.
        // For example:
        // HttpEntity<MultiValueMap<String, String>> oidcRequest = new HttpEntity<>(oidcForm, oidcHeaders);
        // restTemplate.exchange(oidcEndSessionEndpoint, HttpMethod.POST, oidcRequest, String.class);
        // We'll assume this call is successful.

        // --- Front-Channel Logout to SiteMinder (triggers browser redirect) ---
        String ssoLogoutTarget = UriComponentsBuilder.fromUriString(postLogoutSMSessionUrl)
                .queryParam("target", postLogoutRedirectUri)
                .build()
                .toUriString();

        HttpHeaders redirectHeaders = new HttpHeaders();
        redirectHeaders.setLocation(URI.create(ssoLogoutTarget));

        return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);

//        String idToken = body.get("id_token");
//        if (idToken == null || idToken.isBlank()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setBasicAuth(clientId, clientSecret);
//
//        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
//        form.add("id_token", idToken);
//
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);
//
//        try {
//            ResponseEntity<String> stsResponse = restTemplate.exchange(
//                    logoutUrl,
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class
//            );
//
//            // Always redirect to the frontend's signout landing page
////            HttpHeaders redirectHeaders = new HttpHeaders();
////            String finalRedirectUri = UriComponentsBuilder.fromUriString(postLogoutRedirectUri)
////                    .queryParam("post_logout", "true")
////                    .build()
////                    .toUriString();
////            redirectHeaders.setLocation(URI.create(finalRedirectUri ));
////            return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//        return ResponseEntity.ok().build();
    }
}

