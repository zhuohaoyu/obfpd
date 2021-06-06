package main.java.client;

import com.kitfox.svg.Use;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    ArrayList<OBECourse> courses;

    public String getUsername() {
        return Username;
    }

    public ArrayList<OBECourse> getCourses() {
        return courses;
    }

    public OBEManager() {
        LoginWorker = new OBELoginWorker();
        courses = new ArrayList<>();
    }

    public OBEManager(String username, String password) {
        Username = username;
        Password = password;
        LoginWorker = new OBELoginWorker();
        courses = new ArrayList<>();
    }

    public void setInfo(String username, String password) {
        Username = username;
        Password = password;
    }

    public boolean doTest() throws IOException {
        Document document = Jsoup.connect(OBEConstants.OBE_URL).cookies(Cookie).get();
        if (document.title().toString().equals(OBEConstants.SUCC_LOGIN)) return true;
        else return false;
    }

    public boolean doLogin() {
        try {
            Cookie = LoginWorker.getCookie(Username, Password);
            return doTest();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void uploadHomework(String path, String courseID, int homeworkID) {
        File uploadFile = new File(path);
        try {
            FileInputStream fis = new FileInputStream(uploadFile);
            Connection.Response response =
                    Jsoup.connect("http://obe.ruc.edu.cn/index/homework/upload.html")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .cookies(Cookie)
                    .data("cno", courseID)
                    .data("hno", String.valueOf(homeworkID))
                    .data("upload", uploadFile.getName(), fis)
                    .method(Connection.Method.POST)
                    .execute();
            System.out.println(response.body());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteHomework(String courseID, int homeworkID) {
        try {
            Connection.Response response =
                    Jsoup.connect("http://obe.ruc.edu.cn/index/homework/deleteHomework.html")
                            .ignoreContentType(true)
                            .cookies(Cookie)
                            .data("cno", courseID)
                            .data("hno", String.valueOf(homeworkID))
                            .method(Connection.Method.POST)
                            .execute();
            System.out.println(response.body());
        } catch(Exception e) {
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
                courses.add(curcourse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDataFolders(String path) {
        System.out.println(System.getProperty("user.dir"));
        Path dataPath = null;
        Pattern illegalFilePat = Pattern.compile("[\\\\/:*?\"<>| ]");

        if(path == null || path.length() < 1) {
            dataPath = Paths.get(System.getProperty("user.dir"), "OBFPDdata", Username);
            System.out.println(dataPath);
        }
        else {
            dataPath = Paths.get(path, "OBFPDdata", Username);
            System.out.println(dataPath);
        }
        try{
            for(int i = 0; i < courses.size(); ++i) {
                OBECourse curCourse = courses.get(i);
                String fixedCourseName = illegalFilePat.matcher(curCourse.getCourseName()).replaceAll("");
                Path coursePath = Paths.get(dataPath.toString(), fixedCourseName);
                Path coursePathCreate = Files.createDirectories(coursePath);

                for(int j = 0; j < curCourse.homework.size(); ++j) {
                    OBEHomework curHomework = curCourse.homework.get(j);
                    String fixedHwName = illegalFilePat.matcher(curHomework.getTitle()).replaceAll("");
                    Path hwPath = Paths.get(coursePathCreate.toString(), fixedHwName);
                    Path hwPathCreate = Files.createDirectories(hwPath);
                    curHomework.setLocalPath(hwPathCreate.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    public static void main(String args[]) {
        OBEManager manager = new OBEManager("2019201409", "keaiwangyuansen");
        manager.doLogin();
        manager.getContent();

//        manager.uploadHomework("C:\\Users\\zhuoh\\Desktop\\Docs\\EDA_homework\\baseline.pdf", "20193oxjr00h583s", 2054);
//        manager.deleteHomework("20193oxjr00h583s", 2054);

//        manager.doLogin();
//        manager.getContent();
    }
 */
}
