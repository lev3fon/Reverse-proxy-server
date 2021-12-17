package com.example.reverseproxyserver;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.example.reverseproxyserver.models.Redirect;
import com.example.reverseproxyserver.postgres.PgConnection;
import com.sun.net.httpserver.HttpServer;

public class ReverseProxyServerApplication {
    private static HttpServer server;
    private static ArrayList<String> contextUrlList;

    public static void main(String[] args) throws Exception {
        contextUrlList = new ArrayList<>();
        PgConnection pgConn = new PgConnection();
        int port = 8000;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        reloadServer();
        server.start();
    }

    static void addRedirectUrl(Redirect redirect) {
        if (!redirect.isActive) {
            return;
        }
        contextUrlList.add(redirect.from);
        server.createContext(redirect.from, new CustomHttpRequestHandler(redirect));
    }

    static void reloadServer() {
        contextUrlList.forEach(context -> server.removeContext(context));
        contextUrlList = new ArrayList<>();
        ArrayList<Redirect> urlTables = PgConnection.listRedirects();
        urlTables.forEach(ReverseProxyServerApplication::addRedirectUrl);
        server.createContext("/redirects", new AdminHandler(ReverseProxyServerApplication::reloadServer));
    }
}