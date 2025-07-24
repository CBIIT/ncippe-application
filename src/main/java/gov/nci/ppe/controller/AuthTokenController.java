package gov.nci.ppe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/publicapi/auth")
public class AuthTokenController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.logingov.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.logingov.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.logingov.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.logingov.scope}")
    private String scope;

    @Value("${spring.security.oauth2.client.registration.logingov.redirect-uri}")
    private String redirectUri;

    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", scope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }
}
