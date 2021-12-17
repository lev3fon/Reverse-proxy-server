package com.example.reverseproxyserver.postgres;

import com.example.reverseproxyserver.models.Redirect;
import com.example.reverseproxyserver.models.RequestLog;
import com.example.reverseproxyserver.models.User;
import com.example.reverseproxyserver.Utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class PgConnection {
    private static Connection pgConn;

    public PgConnection() throws Exception {
        PgConfig config = ConfigReader.getConfig();
        String pgUrl = String.format("jdbc:postgresql://%s:%s/", config.host, config.port);
        Properties props = new Properties();
        props.setProperty("user", config.username);
        props.setProperty("password", config.password);

        pgConn = DriverManager.getConnection(pgUrl, props);
    }

    public static ArrayList<Redirect> listRedirects() {
        ArrayList<Redirect> result = new ArrayList<>();
        String query = "SELECT * FROM public.redirects;";
        try {
            Statement st = pgConn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                result.add(new Redirect(rs));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void createRedirect(String from, String to, boolean isActive) {
        String query = String.format("INSERT into public.redirects (\"from\", \"to\", \"isActive\") values ('%s', '%s', %s);",
                Utils.clear(from),
                Utils.clear(to),
                isActive
        );
        try {
            Statement st = pgConn.createStatement();
            st.executeUpdate(query);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateRedirect(int redirectId, String from, String to, boolean isActive) {
        String query = String.format("UPDATE public.redirects set \"from\"='%s', \"to\"='%s', \"isActive\"=%s where \"id\"=%d",
                Utils.clear(from),
                Utils.clear(to),
                isActive,
                redirectId
        );
        try {
            Statement st = pgConn.createStatement();
            st.executeUpdate(query);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteRedirect(int redirectId) {
        String query = String.format("DELETE from public.redirects where \"id\"=%d",
                redirectId
        );
        try {
            Statement st = pgConn.createStatement();
            st.executeUpdate(query);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<RequestLog> listRequestLogs() {
        ArrayList<RequestLog> result = new ArrayList<>();
        String query = "SELECT * FROM public.logs;";
        try {
            Statement st = pgConn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                result.add(new RequestLog(rs));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void createRequestLog(int redirectId, String origin, String userAgent) {
        Date currentDate = new Date();
        String query = String.format("INSERT into public.logs (\"createdAt\", \"redirectId\", \"origin\", \"userAgent\") values ('%s', '%d', '%s', '%s');",
                currentDate,
                redirectId,
                Utils.clear(origin),
                Utils.clear(userAgent));
        try {
            Statement st = pgConn.createStatement();
            st.executeUpdate(query);
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean chechUserRole(String username, String password) {
        String query = String.format("SELECT * from public.users where \"username\"='%s' and \"password\"='%s'",
                Utils.clear(username),
                Utils.clear(password)
        );
        try {
            Statement st = pgConn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                return true;
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

/*
CREATE TABLE users (
    "id" serial not null primary key,
    "username" varchar(255) NOT NULL,
    "password" varchar(255) NOT NULL,
	"isAdmin" bool
);

INSERT INTO users (id, username, password, "isAdmin")
VALUES (1, 'admin', '123', true)
*/
