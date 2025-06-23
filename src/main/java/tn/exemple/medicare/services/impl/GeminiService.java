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
    private  final DoctorServices doctorServices;

    @Override
    public String checkInteractions(String userPrompt) {
        String url = "https://openrouter.ai/api/v1/chat/completions";

        // Corps de la requête
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek/deepseek-chat-v3-0324:free");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "user",
                "content", userPrompt
        ));
        body.put("messages", messages);

        // Headers HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.add("HTTP-Referer", "https://votresite.com"); // facultatif
        headers.add("X-Title", "MediCare App"); // facultatif

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
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String content = (String) message.get("content");
                    return content;
                }
            }
            return "No response from DeepSeek.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling DeepSeek API: " + e.getMessage();
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
