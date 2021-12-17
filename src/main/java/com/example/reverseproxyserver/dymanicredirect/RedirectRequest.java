package com.example.reverseproxyserver.dymanicredirect;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RedirectRequest {
    public RedirectRequest(HttpExchange exchange, String nextRequestUrl) throws IOException {
        URL url = new URL(nextRequestUrl + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        exchange.getRequestHeaders().forEach((headerName, headersValue) -> {
            if (headersValue.isEmpty()) {
                return;
            }
            connection.setRequestProperty(headerName, headersValue.get(0));
        });
        connection.setRequestMethod(exchange.getRequestMethod());

        if (exchange.getRequestMethod().equals("POST")) {
            connection.setDoOutput(true);
            String requestBody = exchange.getRequestBody().toString();
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes();
                os.write(input, 0, input.length);
            }
        }

        int status = connection.getResponseCode();
        String contentType = connection.getHeaderField("Content-Type");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseData = new StringBuilder();
        String inputLine;
        while ((inputLine = inputStream.readLine()) != null) {
            responseData.append(inputLine);
        }
        inputStream.close();
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", contentType);
        exchange.sendResponseHeaders(status, responseData.length());

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseData.toString().getBytes());
        outputStream.close();
    }
}
