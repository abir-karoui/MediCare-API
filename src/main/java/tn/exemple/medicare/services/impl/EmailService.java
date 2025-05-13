package tn.exemple.medicare.services.impl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tn.exemple.medicare.enums.TypeCode;

import java.nio.charset.StandardCharsets;
@Service
public class EmailService {
     private final JavaMailSender mailSender;
     public EmailService(JavaMailSender mailSender) {
          this.mailSender = mailSender;
     }
     public void sendEmail(
             String to,
             String username,
             String code,
             String subject,
             TypeCode typeCode
     ) throws MessagingException {
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                  mimeMessage,
                  MimeMessageHelper.MULTIPART_MODE_MIXED,
                  StandardCharsets.UTF_8.name()
          );

          String text;
          if (typeCode == TypeCode.ACTIVATION) {
               text = "Good Morning " + username + ",\n\n"
                       + "Here is your activation code: " + code + "\n"
                       + "Best regards,\n"
                       + "The Medicare Support Team";
          } else if (typeCode == TypeCode.RESET) {
               text = "Good Morning " + username + ",\n\n"
                       + "Here is your password reset code: " + code + "\n"
                       + "Best regards,\n"
                       + "The Medicare Support Team";
          }else {
               throw new IllegalArgumentException("Type de code inconnu");
          }

          helper.setFrom("abir.belkaroui@gmail.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(text, false);

          mailSender.send(mimeMessage);
     }
     @Async
     public void sendSimpleMessage(String to, String username, String subject, String messageBody) throws MessagingException {
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                  mimeMessage,
                  MimeMessageHelper.MULTIPART_MODE_MIXED,
                  StandardCharsets.UTF_8.name()
          );
          String text = "Good Morning " + username + ",\n\n" +
                  messageBody + "\n\n" +
                  "Best regards,\n" +
                  "The Medicare Support Team";
          helper.setFrom("abir.belkaroui@gmail.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(text, false);

          mailSender.send(mimeMessage);
     }

}
