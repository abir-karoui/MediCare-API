package tn.exemple.medicare.services.impl;

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

    @Override
    public String checkInteractions(String userPrompt) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "google/gemini-2.5-pro-preview");
        body.put("max_tokens", 1000);

        // Préparer le message utilisateur
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", userPrompt);

        message.put("content", Collections.singletonList(textContent));
        body.put("messages", List.of(message));

        // Préparer les headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.add("HTTP-Referer", "https://votresite.com");
        headers.add("X-Title", "MediCare App");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> messageResponse = (Map<String, Object>) firstChoice.get("message");
                    Object content = messageResponse.get("content");

                    if (content instanceof List) {
                        List<Map<String, String>> contentList = (List<Map<String, String>>) content;
                        return contentList.get(0).get("text");
                    } else if (content instanceof String) {
                        return (String) content;
                    } else {
                        return "Réponse inattendue de Gemini.";
                    }
                }
            }
            return "Pas de réponse de Gemini.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'appel à Gemini: " + e.getMessage();
        }
    }

    public String validateNewMedication(String newMedication) {
        List<Prescription> prescriptions = prescriptionServices.getPrescriptions();

        List<String> medicationNames = prescriptions.stream()
                .map(prescription -> prescription.getMedication().getDenomination())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //medicationNames.add(newMedication);

        String prompt = "The patient is currently taking the following medications: " +
                String.join(", ", medicationNames) +
                ". They want to add this medication: " + newMedication + ". " +
                "Is there a risk of drug interaction between this new medication and the others? " +
                "Respond only with a single line in English:" +
                "-Warning: <brief description of the interaction>" +
                "no interaction detected." +
                "Do not provide any explanation or reasoning.";




        return checkInteractions(prompt);
    }
}
