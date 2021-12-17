package com.example.reverseproxyserver;

import com.example.reverseproxyserver.models.Redirect;
import com.example.reverseproxyserver.dymanicredirect.RedirectRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

import static com.example.reverseproxyserver.postgres.PgConnection.createRequestLog;

class CustomHttpRequestHandler implements HttpHandler {
    final String url;
    final String redirectUrl;
    final int redirectId;

    public CustomHttpRequestHandler(Redirect redirect) {
        this.url = redirect.from;
        this.redirectUrl = redirect.to;
        this.redirectId = redirect.id;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String nextRequestUrl = getRedirectUrl(exchange.getRequestURI().toString());
        createRequestLog(
                redirectId,
                exchange.getRemoteAddress().toString(),
                exchange.getRequestHeaders().getFirst("User-Agent")
        );
        new RedirectRequest(exchange, nextRequestUrl);
    }

    private String getRedirectUrl(String requestUrl) {
        return requestUrl.replace(this.url, this.redirectUrl);
    }
}
