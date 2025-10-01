package com.agrosmart.middleware.controllers;

import com.agrosmart.middleware.model.Request;
import com.agrosmart.middleware.model.Response;
import com.agrosmart.middleware.services.interfaces.IGeminiRequestService;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/recommendation")
public class RecommendationController {

    @Autowired
    private IGeminiRequestService grs;

    @PostMapping("/getRecommendation")
    public CompletableFuture<ResponseEntity<Response>> getRecommendation(@RequestBody Request request) {
        return grs.getPromptResponse(request)
                .thenApply(response -> {
                    if (response.getResponse().startsWith("Error")) {
                        return ResponseEntity.internalServerError().body(response);
                    }
                    return ResponseEntity.ok(response);
                });
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
