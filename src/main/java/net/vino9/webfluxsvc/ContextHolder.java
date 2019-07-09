package net.vino9.webfluxsvc;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

/*
 singleton scope context holder is probably the wrong design but it's here just to illustrate the
 propagation of trace header for testing
 */
@Component
public class ContextHolder {

    public static final String[] CONTEXT_HEADERS = {"X-B3-TraceId", "X-B3-SpanId"};

    private HashMap<String, String> context = new HashMap<>();

    public void storeContext(ServerRequest request) {
        ServerRequest.Headers headers = request.headers();

        for (String key : CONTEXT_HEADERS) {
            String value = headers.header(key).isEmpty() ? "" : headers.header(key).get(0);
            context.put(key, value);
        }
    }

    ;

    public Map<String, String> getContext() {
        return context;
    }
}
