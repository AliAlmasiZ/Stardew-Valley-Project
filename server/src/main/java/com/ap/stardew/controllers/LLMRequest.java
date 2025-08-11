package com.ap.stardew.controllers;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class LLMRequest {
    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public LLMRequest() {
        Dotenv dotenv = Dotenv.load();
        apiKey = dotenv.get("METIS_API_KEY");

        System.out.println("API Key : " + apiKey);

        model = "gpt-4.1-mini";
        baseUrl = "https://api.metisai.ir/openai/v1/chat/completions";
    }

    public LLMRequest(String apiKey, String model, String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    public String talkToLLM(String sysMsg, String userMsg) throws IOException, InterruptedException {
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("max_tokens", 100);
        payload.put("temperature", 0.1);

        JSONArray messages = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", sysMsg);
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userMsg);
        messages.put(systemMessage);
        messages.put(userMessage);

        payload.put("messages", messages);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200) {
            System.err.println("API request failed with status code: " + response.statusCode());
            System.err.println(response.body());
            return "I don't know";
        }

        JSONObject result = new JSONObject(response.body());
        var choices = result.getJSONArray("choices");
        var first = choices.getJSONObject(0);
        JSONObject message = first.getJSONObject("message");
        return message.getString("contetn").trim();
    }

}
