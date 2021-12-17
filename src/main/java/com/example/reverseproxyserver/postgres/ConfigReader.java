package com.example.reverseproxyserver.postgres;

import org.json.simple.JSONObject;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;

public class ConfigReader
{
    public static PgConfig getConfig()
    {
        JSONParser configParser = new JSONParser();
        try (FileReader reader = new FileReader("config.json"))
        {
            Object obj = configParser.parse(reader);
            JSONObject config = (JSONObject) obj;
            return new PgConfig((JSONObject) config.get("postgres"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PgConfig();
    }

}