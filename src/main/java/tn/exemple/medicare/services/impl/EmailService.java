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
               text = "Bonjour " + username + ",\n\n"
                       + "Voici votre code d'activation : " + code + "\n"
                       + "Cordialement,\n"
                       + "L'équipe d'assistance technique de Medicare";
          } else if (typeCode == TypeCode.RESET) {
               text = "Bonjour " + username + ",\n\n"
                       + "Voici votre code de réinitialisation : " + code + "\n"
                       + "Cordialement,\n"
                       + "L'équipe d'assistance technique de Medicare";
          }else {
               throw new IllegalArgumentException("Type de code inconnu");
          }

          helper.setFrom("abir.belkaroui@gmail.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(text, false);

          mailSender.send(mimeMessage);
     }
}
