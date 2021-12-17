package com.example.reverseproxyserver;

public class Utils {
    public static String clear(String str) {
        return str.replaceAll("\"'\\*","");
    }
}
