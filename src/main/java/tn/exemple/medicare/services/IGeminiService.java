package tn.exemple.medicare.services;

public interface IGeminiService {
    public String checkInteractions(String userPrompt);
    String validateNewMedication(Long idPatient , String newMedication);
}
