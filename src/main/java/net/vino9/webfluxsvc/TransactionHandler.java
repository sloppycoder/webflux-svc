package net.vino9.webfluxsvc;

import lombok.extern.slf4j.Slf4j;
import net.vino9.webfluxsvc.service.Transaction;
import net.vino9.webfluxsvc.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Slf4j
public class TransactionHandler {

    TransactionService transactionService;

    public TransactionHandler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Mono<ServerResponse> findOne(ServerRequest request) {

        String id = request.pathVariable("id");
        log.info("findOne({})", id);

        return transactionService.findById(Long.valueOf(id))
                .flatMap( t -> ok().body(BodyInserters.fromObject(t)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        log.info("findAll");

        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(transactionService.getAll(), Transaction.class);
    }
}


