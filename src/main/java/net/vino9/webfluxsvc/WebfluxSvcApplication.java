package net.vino9.webfluxsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@SpringBootApplication
public class WebfluxSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxSvcApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> routes(TransactionHandler handler) {
		return RouterFunctions.route()
				.GET("/transactions/{id}", accept(MediaType.APPLICATION_JSON), handler::findOne)
				.GET("/transactions", accept(MediaType.APPLICATION_JSON), handler::findAll)
				.build();
	}

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}
}
