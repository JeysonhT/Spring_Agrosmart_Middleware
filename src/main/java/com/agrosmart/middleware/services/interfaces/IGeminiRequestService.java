package com.agrosmart.middleware.services.interfaces;

import com.agrosmart.middleware.model.Request;
import com.agrosmart.middleware.model.Response;
import java.util.concurrent.CompletableFuture;

public interface IGeminiRequestService {
    // metodo para obtener respuestas asincronas
    CompletableFuture<Response> getPromptResponse(Request request);
}
