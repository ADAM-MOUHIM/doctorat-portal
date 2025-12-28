package com.micro.account.service.mail;

public interface MailSender {
    void send(String to, String subject, String body);
}
