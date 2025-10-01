/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.agrosmart.middleware.services;

import com.agrosmart.middleware.model.Request;
import com.agrosmart.middleware.model.Response;
import com.agrosmart.middleware.services.interfaces.IGeminiRequestService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GeminiRequestService implements IGeminiRequestService {

    private final HttpClient httpClient;

    public GeminiRequestService() {
        httpClient = HttpClient.newHttpClient();
        if (System.getenv("API_KEY") == null || System.getenv("API_KEY").isEmpty()) {
            throw new IllegalStateException("FATAL: La variable de entorno API_KEY no está configurada.");
        }
    }

    @Async
    @Override
    public CompletableFuture<Response> getPromptResponse(Request request) {

        try {
            // Body de la peticion http
            JSONObject textpart = new JSONObject().put("text", request.getRequest());
            JSONArray parts = new JSONArray().put(textpart);

            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);

            JSONObject object = new JSONObject().put("contents", contents);

            // Leer la variable de entorno del sistema
            String apiKey = System.getenv("API_KEY");
            String uri = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

            var httpRequest = HttpRequest.newBuilder(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(object.toString()))
                    .build();

            var httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + httpResponse.statusCode());
            System.out.println("Raw body: " + httpResponse.body());

            // procesamiento de la respuesta con JsonNode
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(httpResponse.body());

            String text = root
                    .get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText();

            // devolvermos el completable future para su posterior consulta por el controlador
            return CompletableFuture.completedFuture(new Response(text));

        } catch (IOException | InterruptedException e) {
            return CompletableFuture.
                    completedFuture(new Response("Error al obtener la respuesta: " + e.getMessage()));
        }

    }
}
