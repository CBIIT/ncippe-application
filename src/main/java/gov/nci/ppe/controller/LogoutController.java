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

import java.net.URI;
import java.util.Map;

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

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/logout-sts")
    public ResponseEntity<Void> logoutSTS(@RequestBody Map<String, String> body) {
        String idToken = body.get("id_token");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("id_token", idToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> stsResponse = restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Always redirect to the frontend's signout landing page
            HttpHeaders redirectHeaders = new HttpHeaders();
            String finalRedirectUri = UriComponentsBuilder.fromUriString(postLogoutRedirectUri)
                    .queryParam("post_logout", "true")
                    .build()
                    .toUriString();
            redirectHeaders.setLocation(URI.create(finalRedirectUri ));
            return new ResponseEntity<>(redirectHeaders, HttpStatus.FOUND);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

