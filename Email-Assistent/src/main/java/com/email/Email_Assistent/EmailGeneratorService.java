package com.email.Email_Assistent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailGeneratorService {
    //taking the webclient instance
    private final WebClient webClient;
    private final String apiKey;
    public EmailGeneratorService(WebClient.Builder webClientBuilder,
                                 @Value("${gemini.api.url}") String baseUrl,
                                 @Value("${gemini.api.key}") String geminiApiKey ) {
        this.apiKey = geminiApiKey;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }


public String generateEmailReply(EmailRequest emailRequest){
    //logic of the geerating Response based on Tone
//    steps--> 1.Build Prompt

    String prompt = BuildPrompt(emailRequest);
    //         2.Prepare our own JSON Body based on the API Structure(google API)
    //         3.Send Request
    //         4.Extract Response

    //prepare our own raw-json body
    String requestBody = String.format("""
            {
                "contents": [
                  {
                    "parts": [
                      {
                        "text": "%s"
                      }
                    ]
                  }
                ]
            }
            """, prompt);
    //NEXT STEP :-> Sent request to the google server
    String response = webClient.post().uri(uriBuilder -> uriBuilder.path("/v1beta/models/gemini-2.5-flash:generateContent")
            .build()).header("x-goog-api-key", apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class).block();

    //TO read the response we need a metod:->
    return  extractResponseContent(response);
}
    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            return root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //future implementation as like-credit limit
    }

    private String BuildPrompt(EmailRequest emailRequest) {
     StringBuilder prompt = new StringBuilder();
     prompt.append("Generate a Professional email reply for the following email :");
     //validation check :condition wise
        if(emailRequest.getTone()!= null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a").append(emailRequest.getTone()).append(" tone. ");
            //dynamically replaced or designed this string by tone
        }
        prompt.append("Original Email: \n").append(emailRequest.getEmailContent());
        //return as string
        return prompt.toString();
    }
}
