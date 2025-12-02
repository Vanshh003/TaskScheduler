package com.pers.taskScheduler.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookTaskHandler {

    private final WebClient webClient;

    public int executeWebhook(String url, String payload) {

        try {
            return webClient.post()
                    .uri(url)
                    .bodyValue(payload == null ? "" : payload)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> {
                        log.info("Webhook call succeeded with status {}", response.getStatusCode().value());
                        return response.getStatusCode().value();
                    })
                    .onErrorResume(e -> {
                        log.error("Webhook call failed: {}", e.getMessage());
                        return Mono.just(500);
                    })
                    .block()
                    .intValue();

        } catch (Exception e) {
            log.error("Unexpected webhook error: {}", e.getMessage());
            return 500;
        }
    }
}
