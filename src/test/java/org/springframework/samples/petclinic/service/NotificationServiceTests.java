package org.springframework.samples.petclinic.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    private MimeMessage mockMimeMessage;

    @BeforeEach
    void setUp() {
        mockMimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);
    }

    @Test
    void shouldSendHtmlEmailSuccessfully() throws Exception {
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Test User");
        String htmlContent = "<html><body>Hello <span th:text=\"${\${name}}\"></span></body></html>";

        when(templateEngine.process(eq(templateName), any())).thenReturn(htmlContent);

        notificationService.sendHtmlEmail(to, subject, templateName, variables);

        verify(mailSender, times(1)).createMimeMessage();
        verify(templateEngine, times(1)).process(eq(templateName), any());
        verify(mailSender, times(1)).send(mimeMessageCaptor.capture());

        MimeMessage sentMessage = mimeMessageCaptor.getValue();
        assertThat(sentMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString()).isEqualTo(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getContent().toString()).contains("Hello Test User");
    }

    @Test
    void shouldThrowRuntimeExceptionOnMessagingException() throws Exception {
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        Map<String, Object> variables = new HashMap<>();

        // Simulate a MessagingException during MimeMessageHelper setup
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("MimeMessage creation error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                notificationService.sendHtmlEmail(to, subject, templateName, variables)
        );

        assertThat(thrown.getMessage()).contains("Failed to send email due to messaging error");
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void shouldThrowRuntimeExceptionOnMailException() throws Exception {
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        Map<String, Object> variables = new HashMap<>();
        String htmlContent = "<html><body>Hello</body></html>";

        when(templateEngine.process(eq(templateName), any())).thenReturn(htmlContent);
        doThrow(new org.springframework.mail.MailSendException("Mail send error")).when(mailSender).send(any(MimeMessage.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                notificationService.sendHtmlEmail(to, subject, templateName, variables)
        );

        assertThat(thrown.getMessage()).contains("Failed to send email");
        verify(mailSender, times(1)).createMimeMessage();
        verify(templateEngine, times(1)).process(eq(templateName), any());
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}