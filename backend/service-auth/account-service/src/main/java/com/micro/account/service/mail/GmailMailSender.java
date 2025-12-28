package com.micro.account.service.mail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class GmailMailSender implements MailSender {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String from;
    private final HttpClient httpClient;

    public GmailMailSender(
            @Value("${app.gmail.client-id:}") String clientId,
            @Value("${app.gmail.client-secret:}") String clientSecret,
            @Value("${app.gmail.refresh-token:}") String refreshToken,
            @Value("${app.gmail.from:noreply@example.com}") String from
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.from = from;
        this.httpClient = HttpClient.newHttpClient();
    }

    private boolean configured() {
        return !isBlank(clientId) && !isBlank(clientSecret) && !isBlank(refreshToken);
    }

    @Override
    public void send(String to, String subject, String body) {
        if (!configured()) {
            log.warn("Mail not sent: Gmail credentials missing (clientId/clientSecret/refreshToken).");
            return;
        }
        try {
            String accessToken = fetchAccessToken();
            if (accessToken == null) {
                log.warn("Mail not sent: could not obtain access token.");
                return;
            }
            String rawMessage = buildRawMessage(to, subject, body);
            String payload = "{\"raw\":\"" + rawMessage + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                log.error("Failed to send email via Gmail API. Status: {} Body: {}", response.statusCode(), response.body());
            }
        } catch (Exception ex) {
            log.error("Error sending email via Gmail API", ex);
        }
    }

    private String fetchAccessToken() throws Exception {
        String form = "client_id=" + encode(clientId)
                + "&client_secret=" + encode(clientSecret)
                + "&refresh_token=" + encode(refreshToken)
                + "&grant_type=refresh_token";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 300) {
            log.error("Failed to fetch access token. Status: {} Body: {}", resp.statusCode(), resp.body());
            return null;
        }

        String body = resp.body();
        try {
            Map<String, Object> json = MAPPER.readValue(body, new TypeReference<>() {});
            Object token = json.get("access_token");
            return token != null ? token.toString() : null;
        } catch (Exception ex) {
            log.error("Failed to parse access token response: {}", body, ex);
            return null;
        }
    }

    private String buildRawMessage(String to, String subject, String text) {
        String msg = "From: " + from + "\r\n" +
                "To: " + to + "\r\n" +
                "Subject: " + subject + "\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "\r\n" +
                text;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(String val) {
        return URLEncoder.encode(val, StandardCharsets.UTF_8);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
