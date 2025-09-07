package tn.exemple.medicare.services.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.exemple.medicare.configs.AuthService;
import tn.exemple.medicare.entities.prescription.Prescription;
import tn.exemple.medicare.services.IGeminiService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeminiService implements IGeminiService {



    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private  final PrescriptionServices prescriptionServices;
    private  final DoctorServices doctorServices;

   /* @Override
    public String checkInteractions(String userPrompt) {
        String url = "https://openrouter.ai/api/v1/chat/completions"; // ✅ bon endpoint

        // Corps de la requête
        Map<String, Object> body = new HashMap<>();
        body.put("model", "google/gemini-2.5-flash-image-preview:free");

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "user",
                "content", userPrompt
        ));
        body.put("messages", messages);

        // Headers HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.add("HTTP-Referer", "http://localhost:8080"); // 👈 ou ton domaine
        headers.add("X-Title", "MediCare App");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // Récupérer la réponse brute en String
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);

                if (responseBody.containsKey("choices")) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        return (String) message.get("content");
                    }
                }
            }

            return "No response from Gemini.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling Gemini API: " + e.getMessage();
        }
    }
*/
   @Override
   public String checkInteractions(String userPrompt) {
       // ✅ Endpoint officiel Google Gemini
       String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

       // ✅ Corps de la requête
       Map<String, Object> body = new HashMap<>();
       Map<String, Object> content = new HashMap<>();
       content.put("parts", List.of(Map.of("text", userPrompt)));
       body.put("contents", List.of(content));

       // ✅ Headers
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);

       HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

       try {
           ResponseEntity<String> response = restTemplate.exchange(
                   url,
                   HttpMethod.POST,
                   request,
                   String.class
           );

           if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
               ObjectMapper mapper = new ObjectMapper();
               Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);

               if (responseBody.containsKey("candidates")) {
                   List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                   if (!candidates.isEmpty()) {
                       Map<String, Object> candidate = candidates.get(0);
                       Map<String, Object> contentResp = (Map<String, Object>) candidate.get("content");
                       List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResp.get("parts");
                       if (!parts.isEmpty()) {
                           return (String) parts.get(0).get("text");
                       }
                   }
               }
           }

           return "No response from Gemini.";
       } catch (Exception e) {
           e.printStackTrace();
           return "Error calling Gemini API: " + e.getMessage();
       }
   }

    public String validateNewMedication(Long idPatient , String newMedication) {
        List<Prescription> prescriptions = doctorServices.getAllPrescriptionsForPatient(idPatient);

        List<String> medicationNames = prescriptions.stream()
                .map(prescription -> prescription.getMedication().getDenomination())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String prompt = "The patient is currently taking the following medications: " +
                String.join(", ", medicationNames) + ".\n" +
                "They want to add this new medication: " + newMedication + ".\n" +
                "Is there any known drug interaction between this new medication and the current ones?\n" +
                "Respond in English using ONLY ONE of the following formats:\n" +
                "-Warning: <short description of the interaction>\n" +
                "-OK: no interaction detected.\n" +
                "Do not explain. Do not analyze. Do not reason. Just give the final answer in one line.";

        return checkInteractions(prompt);
    }
}


