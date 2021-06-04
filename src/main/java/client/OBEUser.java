package main.java.client;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;

public class OBEUser {
    OBELoginWorker LoginWorker;
    String Username;
    String Password;
    Map<String, String> Cookie;
    public OBEUser(String username, String password) {
        Username = username;
        Password = password;
        LoginWorker = new OBELoginWorker();
    }
    public void doLogin() {
        try {
            Cookie = LoginWorker.getCookie(Username, Password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void crawlAllCoursework() {

    }

}
