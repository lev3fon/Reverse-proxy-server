package com.example.reverseproxyserver.postgres;

import org.json.simple.JSONObject;

public class PgConfig {
    public String host;
    public String port;
    public String name;
    public String password;
    public String username;

    public PgConfig(JSONObject pgConfig) {
        this.host = (String) pgConfig.get("host");
        this.name = (String) pgConfig.get("name");
        this.password = (String) pgConfig.get("password");
        this.port = (String) pgConfig.get("port");
        this.username = (String) pgConfig.get("username");
    }

    public PgConfig() {}
}
