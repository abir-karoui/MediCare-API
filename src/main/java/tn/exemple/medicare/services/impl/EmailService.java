package tn.exemple.medicare.services.impl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
@Service
public class EmailService {
     private final JavaMailSender mailSender;
     public EmailService(JavaMailSender mailSender) {
          this.mailSender = mailSender;
     }

     @Async
     public void sendEmail(
             String to,
             String username,
             String confirmationUrl,
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
                  + "Merci de vous être inscrit. Voici votre code d'activation : " + activationCode + "\n"
                  + "Pour confirmer votre compte, cliquez sur le lien suivant : " + confirmationUrl + "\n\n"
                  + "Cordialement,\n"
                  + "L'équipe de support";

          helper.setFrom("abir.belkaroui@gmail.com");
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(text, false);

          mailSender.send(mimeMessage);
     }
     /*private final SpringTemplateEngine springTemplateEngine;

     @Async
     public void sendEmail(
             String to,
             String username,
             EmailTemplateName emailTemplateName,
             String confirmationUrl,
             String activationCode,
             String subject
     ) throws MessagingException {
          String templateName;
          if (emailTemplateName == null) {
               templateName = "confirm-email";
          } else {
               templateName = emailTemplateName.name();

          }
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(
                  mimeMessage,
                  MimeMessageHelper.MULTIPART_MODE_MIXED,
                  StandardCharsets.UTF_8.name());

          Map<String , Object> proporties = new HashMap<>();
          proporties.put("username", username);
          proporties.put("confirmationUrl" , confirmationUrl);
          proporties.put("activation_code" , activationCode);
          Context context = new Context();
          context.setVariables(proporties);

          helper.setFrom("contact@abir.com");
          helper.setTo(to);
          helper.setSubject(subject);

          String template = springTemplateEngine.process(templateName , context);
          helper.setText(template, true);
          mailSender.send(mimeMessage);

     }*/
}
