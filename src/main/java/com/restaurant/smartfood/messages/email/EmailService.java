package com.restaurant.smartfood.messages.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@Slf4j
public class EmailService {


    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String senderAddress;
    @Value("${logo-path}")
    private  String logoPath;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            sendEmailWithHtml(toEmail, subject, body);
        } catch (Exception e) {
            log.warn("Couldn't send mime message with Html, trying simple mail");
            try {
                sendSimpleMessage(toEmail, subject, body);
            } catch (Exception ex) {
                log.error("Mail could not be sent at all for some reason.");
            }
        }
    }

    private void sendSimpleMessage(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isEmpty()) {
            log.warn("Email is blank");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);


        emailSender.send(message);
        log.info("Email was sent to: " + toEmail);
    }

    private void sendEmailWithHtml(String toEmail, String subject, String body) throws MessagingException, UnsupportedEncodingException, MalformedURLException {
        try {
            if (toEmail == null || toEmail.isEmpty()) {
                log.warn("Email is blank");
                return;
            }
            MimeMessage mimeMessage = emailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderAddress, "Smart Food"); // Here comes your name
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject(subject);

            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");

            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<img src=\"cid:image\"><br/><H3>" + body + "</H3>";
            messageBodyPart.setContent(htmlText, "text/html");
            // add it
            multipart.addBodyPart(messageBodyPart);

            // second part (the image)
            messageBodyPart = new MimeBodyPart();

            URL url = new URL(logoPath);
            DataSource fds = new URLDataSource(url);


            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);

            // put everything together
            mimeMessage.setContent(multipart);
            // Send message
            emailSender.send(mimeMessage);

            log.info("Email was sent to: " + toEmail);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }

    }
}
