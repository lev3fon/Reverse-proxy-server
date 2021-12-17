package com.example.reverseproxyserver;

import com.example.reverseproxyserver.models.Redirect;
import com.example.reverseproxyserver.postgres.PgConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static com.example.reverseproxyserver.postgres.PgConnection.*;

class AdminHandler implements HttpHandler {
    private Runnable reloadServer;

    public  AdminHandler(Runnable reloader) {
        reloadServer = reloader;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetRedirect(exchange);
                break;
            case "POST":
                handlePostRedirect(exchange);
                break;
            case "PUT":
                handlePutRedirect(exchange);
                break;
            case "DELETE":
                handleDeleteRedirect(exchange);
                break;
            default:
                break;
        }
    }

    private void handleGetRedirect(HttpExchange exchange) throws IOException {
        String response = "[]";
        if (verifyAdmin(exchange)) {
            List<String> redirects = PgConnection
                    .listRedirects()
                    .stream()
                    .map(Redirect::toJson)
                    .toList();
            response = String.format("[%s]", String.join(",", redirects));
        }
        sendDataAndCloseExchange(exchange, response);
    }

    private void handlePostRedirect(HttpExchange exchange) throws IOException {
        if (verifyAdmin(exchange)) {
            JSONObject data = getRequestBody(exchange);
            String from = (String) data.get("from");
            String to = (String) data.get("to");
            boolean isActive = (boolean) data.get("isActive");
            createRedirect(from, to, isActive);
        }
        String response = "{\"status\":\"ok\"}";
        sendDataAndCloseExchange(exchange, response);
        reloadServer.run();
    }

    private void handlePutRedirect(HttpExchange exchange) throws IOException {
        if (verifyAdmin(exchange)) {
            JSONObject data = getRequestBody(exchange);
            int id = ((Long) data.get("id")).intValue();
            String from = (String) data.get("from");
            String to = (String) data.get("to");
            boolean isActive = (boolean) data.get("isActive");
            updateRedirect(id, from, to, isActive);
        }
        String response = "{\"status\":\"ok\"}";
        sendDataAndCloseExchange(exchange, response);
        reloadServer.run();
    }

    private void handleDeleteRedirect(HttpExchange exchange) throws IOException {
        if (verifyAdmin(exchange)) {
            JSONObject data = getRequestBody(exchange);
            int id = ((Long) data.get("id")).intValue();
            deleteRedirect(id);
        }
        String response = "{\"status\":\"ok\"}";
        sendDataAndCloseExchange(exchange, response);
        reloadServer.run();
    }

    private boolean verifyAdmin(HttpExchange exchange) {
        String username = exchange.getRequestHeaders().getFirst("Username");
        String password = exchange.getRequestHeaders().getFirst("Password");
        return chechUserRole(username, password);
    }

    private JSONObject getRequestBody(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        StringBuilder buffer = new StringBuilder(512);
        int r;
        while ((r = requestBody.read()) != -1) {
            buffer.append((char) r);
        }
        JSONObject data = new JSONObject();
        try {
            data = (JSONObject) (new JSONParser()).parse(buffer.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void sendDataAndCloseExchange(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
