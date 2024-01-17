package dev.ai4j.openai4j.moderation;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.OpenAiHttpException;

public class RequestDisabledModerationAsyncTest {

    private static final String INPUT = "hello";

    private final OpenAiClient client = OpenAiClient.builder()
            .disableRequests()
            .logRequests()
            .logResponses()
            .build();

    @Test
    void testSimpleApi() {

        CompletableFuture<ModerationResult> future = new CompletableFuture<>();


        client.moderation(INPUT)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();

        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }

    @Test
    void testCustomizableApi() {

        ModerationRequest request = ModerationRequest.builder()
                .input(INPUT)
                .build();

        CompletableFuture<ModerationResponse> future = new CompletableFuture<>();


        client.moderation(request)
                .onResponse(future::complete)
                .onError(future::completeExceptionally)
                .execute();


        await()
            .atMost(Duration.ofSeconds(30))
            .until(future::isCompletedExceptionally);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> future.get(30, SECONDS))
            .havingCause()
            .isInstanceOf(OpenAiHttpException.class)
            .withMessage("Requests to the live system are disabled");
    }
}
