package org.springframework.samples.petclinic.system;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}