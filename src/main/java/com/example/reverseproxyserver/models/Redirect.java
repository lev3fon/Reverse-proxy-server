package com.example.reverseproxyserver.models;

import org.json.simple.JSONObject;

import java.sql.ResultSet;

public class Redirect {
    public int id;
    public String from;
    public String to;
    public boolean isActive;

    public Redirect(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        from = rs.getString(2);
        to = rs.getString(3);
        isActive = rs.getBoolean(4);
    }

    public String toJson() {
        JSONObject result = new JSONObject();
        result.put("id", id);
        result.put("from", from);
        result.put("to", to);
        result.put("isActive", isActive);
        return result.toString();
    }
}
