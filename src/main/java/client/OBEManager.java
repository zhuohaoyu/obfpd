package main.java.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OBEManager {
    OBELoginWorker LoginWorker;
    Map<String, String> Cookie;

    String Username;
    String Password;
    HashMap<String, OBECourse> courses;
    public OBEManager(String username, String password) {
        Username = username;
        Password = password;
        LoginWorker = new OBELoginWorker();
        courses = new HashMap<>();
    }
    public void doLogin() {
        try {
            Cookie = LoginWorker.getCookie(Username, Password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getContent() {
        try{
            String url = "http://obe.ruc.edu.cn/index/home/index.html";
            Document document = Jsoup.connect(url).cookies(Cookie).get();
            String cont = document.body().toString();
            String courseregexpat = "<option value=\"(.{2,})\">(.*)</option>";
            Pattern coursepat = Pattern.compile(courseregexpat);
            Matcher m = coursepat.matcher(cont);
            while(m.find()) {
                String courseID = m.group(1);
                String courseName = m.group(2);
                System.out.println(courseName);

                OBECourse curcourse = new OBECourse(courseID, courseName);
                curcourse.getAllHomework(Cookie);
                curcourse.getAllAttachment(Cookie);
                courses.put(curcourse.CourseID, curcourse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        OBEManager manager = new OBEManager("2019201409", "keaiwangyuansen");
        manager.doLogin();
        manager.getContent();
    }
}
