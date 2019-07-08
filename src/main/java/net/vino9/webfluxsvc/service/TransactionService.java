package net.vino9.webfluxsvc.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TransactionService {

    public static final Transaction[] TRANSACTION_REPO = {
            new Transaction(1L, 100.0, "noodle"),
            new Transaction(2L, 500.0, "iphone"),
            new Transaction(3L, 20000.0, "holiday package"),
    };

    public Mono<Transaction> findById(Long id) {
        Optional<Transaction> tr = Stream.of(TRANSACTION_REPO).filter(e -> e.getId() == id).findFirst();
        return tr.isPresent() ? Mono.just(tr.get()) : Mono.empty();
    }

    public Flux<Transaction> getAll() {
        return Flux.fromArray(TRANSACTION_REPO);
    }

}
