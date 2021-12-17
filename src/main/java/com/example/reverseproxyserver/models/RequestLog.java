package com.example.reverseproxyserver.models;

import java.sql.ResultSet;
import java.util.Date;

public class RequestLog {
    public int id;
    public int redirectId;
    public Date createdAt;
    public String origin;
    public String userAgent;

    public RequestLog(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        redirectId = rs.getInt(2);
        createdAt = rs.getDate(3);
        origin = rs.getString(4);
        userAgent = rs.getString(5);
    }
}