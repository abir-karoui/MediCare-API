package tn.exemple.medicare.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tn.exemple.medicare.enums.EmailTemplateName;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
     /*private final JavaMailSender mailSender;
     private final SpringTemplateEngine springTemplateEngine;

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
