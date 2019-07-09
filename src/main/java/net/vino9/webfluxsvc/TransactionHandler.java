package net.vino9.webfluxsvc;

import lombok.extern.slf4j.Slf4j;
import net.vino9.webfluxsvc.service.Transaction;
import net.vino9.webfluxsvc.service.TransactionService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class TransactionHandler {

    TransactionService transactionService;
    ContextHolder contextHolder;
    WebClient webClient;

    public TransactionHandler(TransactionService transactionService, ContextHolder contextHolder, WebClient webClient) {
        this.transactionService = transactionService;
        this.contextHolder = contextHolder;
        this.webClient = webClient;
    }

    public Mono<ServerResponse> findOne(ServerRequest request) {

        contextHolder.storeContext(request);

        String id = request.pathVariable("id");
        log.info("findOne({})", id);

        if ("2".equals(id)) {
            log.info("invoking down stream service...");
            // testing if  the baggage header is passed on the downstream
            webClient.method(HttpMethod.GET)
                    .uri("http://127.0.0.1:8081")
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(log::info);
        }

        return transactionService.findById(Long.valueOf(id))
                .flatMap(t -> ok().body(BodyInserters.fromObject(t)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        log.info("findAll");

        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionService.getAll(), Transaction.class);
    }
}
