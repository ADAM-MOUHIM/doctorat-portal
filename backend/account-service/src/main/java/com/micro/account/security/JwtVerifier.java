package com.micro.account.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtVerifier {

    private final String issuer;
    private final String secret;
    private final long clockSkewSeconds;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Base64.Decoder B64URL = Base64.getUrlDecoder();
    private static final Base64.Encoder B64URL_ENC = Base64.getUrlEncoder().withoutPadding();

    public JwtVerifier(
            @Value("${app.jwt.issuer:serviceAuth}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.clock-skew-seconds:60}") long clockSkewSeconds
    ) {
        this.issuer = issuer;
        this.secret = secret;
        this.clockSkewSeconds = clockSkewSeconds;
    }

    public Map<String, Object> verify(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) throw new IllegalStateException("JWT malformed.");

            String headerB64 = parts[0];
            String payloadB64 = parts[1];
            String sigB64 = parts[2];

            String signingInput = headerB64 + "." + payloadB64;
            String expectedSig = signHmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), secretKeyBytes());
            if (!expectedSig.equals(sigB64)) throw new IllegalStateException("JWT signature invalid.");

            byte[] payloadJson = B64URL.decode(payloadB64);
            @SuppressWarnings("unchecked")
            Map<String, Object> claims = MAPPER.readValue(payloadJson, Map.class);

            long now = Instant.now().getEpochSecond();
            Object iss = claims.get("iss");
            Object exp = claims.get("exp");
            Object iat = claims.get("iat");
            if (iss == null || !issuer.equals(iss.toString())) {
                throw new IllegalStateException("Issuer invalid.");
            }
            if (!(exp instanceof Number)) throw new IllegalStateException("exp missing.");
            if (!(iat instanceof Number)) throw new IllegalStateException("iat missing.");

            long expSec = ((Number) exp).longValue();
            long iatSec = ((Number) iat).longValue();

            if (now > expSec + clockSkewSeconds) throw new IllegalStateException("Token expired.");
            if (iatSec - clockSkewSeconds > now) throw new IllegalStateException("Token not yet valid.");

            return claims;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("JWT invalid.", e);
        }
    }

    private static String signHmacSha256(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return B64URL_ENC.encodeToString(mac.doFinal(data));
    }

    private byte[] secretKeyBytes() {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}

