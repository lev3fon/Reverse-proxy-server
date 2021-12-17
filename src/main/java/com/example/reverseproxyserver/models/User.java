package com.example.reverseproxyserver.models;

import java.sql.ResultSet;

public class User {
    public int id;
    public String username;
    private String password;
    public boolean isAdmin;

    public User() {}

    public User(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        username = rs.getString(2);
        password = rs.getString(3);
        isAdmin = rs.getBoolean(4);
    }
}
