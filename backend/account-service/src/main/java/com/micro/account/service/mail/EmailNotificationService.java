package com.micro.account.service.mail;

import com.micro.account.entity.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class EmailNotificationService {

    private final MailSender mailSender;
    private final String verificationBaseUrl;
    private final String loginUrl;
    private final Duration ttl;

    public EmailNotificationService(
            MailSender mailSender,
            @Value("${app.verification.base-url:http://localhost:8085/accounts/verify-email?token=}") String verificationBaseUrl,
            @Value("${app.front.login-url:http://localhost:4200/login}") String loginUrl,
            @Value("${app.verification.ttl-minutes:1440}") long ttlMinutes
    ) {
        this.mailSender = mailSender;
        this.verificationBaseUrl = verificationBaseUrl;
        this.loginUrl = loginUrl;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public void sendVerification(Account account, String token) {
        String link = verificationBaseUrl + token;
        String subject = "Vérification de votre email";
        String body = "Bonjour " + account.getUsername() + ",\n\n"
                + "Merci pour votre inscription. Merci de confirmer votre email en cliquant sur le lien suivant (valide " + ttl.toHours() + "h) :\n"
                + link + "\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";
        mailSender.send(account.getEmail(), subject, body);
    }

    public void sendApproval(Account account) {
        String subject = "Votre inscription est acceptée";
        String body = "Bonjour " + account.getUsername() + ",\n\n"
                + "Votre compte a été approuvé. Vous pouvez vous connecter : " + loginUrl + "\n\n"
                + "Bonne journée.";
        mailSender.send(account.getEmail(), subject, body);
    }

    public void sendRejection(Account account) {
        String subject = "Votre inscription est refusée";
        String body = "Bonjour " + account.getUsername() + ",\n\n"
                + "Votre demande d'inscription a été refusée.\n\n"
                + "Si vous pensez qu'il s'agit d'une erreur, contactez l'administrateur.";
        mailSender.send(account.getEmail(), subject, body);
    }
}
