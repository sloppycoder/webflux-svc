package net.vino9.webfluxsvc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import lombok.extern.slf4j.Slf4j;
import net.vino9.webfluxsvc.service.Transaction;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class WebfluxSvcApplicationTests {

    @LocalServerPort
    String port;

    @Autowired
    RestTemplateBuilder builder;

    @Autowired
    ContextHolder contextHolder;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8081);

    @Test
    public void contextLoads() {
    }

    @Test
    public void get_one_transaction() {
        ResponseEntity<Transaction> response = builder.build().getForEntity(baseUrl() + "/1", Transaction.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = HttpClientErrorException.class)
    public void invalid_transaction_id_not_found() {
        ResponseEntity<Transaction> response = builder.build().getForEntity(baseUrl() + "/10", Transaction.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_all_transactions() {
        ResponseEntity<List<Transaction>> response = builder.build().exchange(baseUrl(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Transaction>>() {
                });
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(3, response.getBody().size());
    }


    //    This test case shows that when X-B3-xxx headers are provided, the sleuth will use it
    //    instead of generating a new ones. This can be used to implement end-to-end tracability, e.g.
    //    generate trace id from the mobile client, pass them in to our services, then we can have a way
    //    trace from mobile analytics (i.e. Google Analytics) to API services
    //
    //    With sleuth, it is also possible to use configuration spring.sleuth.baggage-keys and
    //    spring.sleuth.propagation-keys to propagate header from span to span
    @Test
    public void custom_traceid_and_additonal_header_to_propagate() throws IOException {
        String customTraceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        log.info("Custom Trace Id = {}", customTraceId);

        // do not use RestTemplate for this test because it is already instructmented by Sleuth
        // and it's tricky to override the headers
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl() + "/2")
                .header("X-B3-TraceId", customTraceId)
                .header("X-B3-SpanId", customTraceId)
                .header("baggage-api-context", customTraceId)
                .header("foo", "bar")
                .build();

        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{ \"id\":100 }")));

        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());

        // without this delay the verification steps will fail
        // perhaps because the webclient is async and the verify is reached before
        // the request is complete?
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // spring.sleuth.baggage-keys
        verify(getRequestedFor(urlMatching("/*"))
                .withHeader("baggage-api-context", matching(".*")));

        // spring.sleuth.propagation-keys
        verify(getRequestedFor(urlEqualTo("/"))
                .withHeader("foo", equalTo("bar")));
    }


    private String baseUrl() {
        return "http://127.0.0.1:" + port + "/transactions";
    }

}