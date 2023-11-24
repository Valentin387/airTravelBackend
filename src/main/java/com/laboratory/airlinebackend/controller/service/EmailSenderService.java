package com.laboratory.airlinebackend.controller.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailSenderService{

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String toEmail,
                                String subject,
                                String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
        //System.out.println("E-Mail Sent...");


    }

    public void sendEmailWithAttachment(String toEmail,
                                        String subject,
                                        String body,
                                        String attachmentName,
                                        byte[] attachment
    ){
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Attach the PDF
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

            mailSender.send(message);
            //System.out.println("E-Mail with attachment Sent...");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
