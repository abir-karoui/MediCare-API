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
             TypeCode typeCode // Ajoute le paramètre TypeCode pour vérifier le type
     ) throws MessagingException {
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                  mimeMessage,
                  MimeMessageHelper.MULTIPART_MODE_MIXED,
                  StandardCharsets.UTF_8.name()
          );

          // Vérifie le type de code et adapte le message en conséquence
          String text;
          if (typeCode == TypeCode.ACTIVATION) {
               text = "Bonjour " + username + ",\n\n"
                       + "Voici votre code d'activation : " + code + "\n"
                       + "Cordialement,\n"
                       + "L'équipe de support";
          } else if (typeCode == TypeCode.RESET) {
               text = "Bonjour " + username + ",\n\n"
                       + "Voici votre code de réinitialisation : " + code + "\n"
                       + "Cordialement,\n"
                       + "L'équipe de support";
          } else {
               text = "Bonjour " + username + ",\n\n"
                       + "Voici votre code : " + code + "\n"
                       + "Cordialement,\n"
                       + "L'équipe de support";
          }

          helper.setFrom("abir.belkaroui@gmail.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(text, false);

          mailSender.send(mimeMessage);
     }

    /* @Async
     public void sendEmail(
             String to,
             String username,
             String activationCode,
             String subject
     ) throws MessagingException {
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                  mimeMessage,
                  MimeMessageHelper.MULTIPART_MODE_MIXED,
                  StandardCharsets.UTF_8.name()
          );

          String text = "Bonjour " + username + ",\n\n"
                  + "Voici votre code d'activation : " + activationCode + "\n"
                  + "Cordialement,\n"
                  + "L'équipe de support";

          helper.setFrom("abir.belkaroui@gmail.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(text, false);

          mailSender.send(mimeMessage);
     }*/
}
