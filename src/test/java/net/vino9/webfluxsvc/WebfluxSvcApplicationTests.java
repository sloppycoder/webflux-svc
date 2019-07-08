package net.vino9.webfluxsvc;

import net.vino9.webfluxsvc.service.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebfluxSvcApplicationTests {

	@LocalServerPort
	String port;

	@Autowired
	RestTemplateBuilder builder;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGetOneTrasnaction() {
		ResponseEntity<Transaction> response = builder.build().getForEntity(baseUrl() + "/1", Transaction.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test(expected = HttpClientErrorException.class)
	public void testInvalidTrasnactionId() {
		ResponseEntity<Transaction> response = builder.build().getForEntity(baseUrl() + "/10", Transaction.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testAllTrasnactions() {
		ResponseEntity<List<Transaction>> response = builder.build().exchange(baseUrl(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Transaction>>() {});
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals(3, response.getBody().size());
	}


	private String baseUrl() {
		return "http://127.0.0.1:" + port + "/transactions";
	}

}
