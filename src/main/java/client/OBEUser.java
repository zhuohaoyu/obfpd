package main.java.client;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOError;
import com.alibaba.fastjson.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.io.*;

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

//    public ArrayList<String> crawlCourseList() {

//    }


    public JSONObject crawlPagedCoursework(int pagenum, int querytype) throws IOException {

        URL url = new URL("http://obe.ruc.edu.cn/index/home/morenews.html");

        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("POST");

        http.setDoOutput(true);

        http.setRequestProperty("Connection", "keep-alive");
        http.setRequestProperty("Accept", "application/json, text/plain, */*");
        http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        http.setRequestProperty("Origin", "http://obe.ruc.edu.cn");
        http.setRequestProperty("Referer", "http://obe.ruc.edu.cn/index/home/index.html");
        http.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        http.setRequestProperty("Cookie", "uid=qukbcm4isj674pl2tdq2v95jm5");

        String rawdata = "page=" + pagenum + "&cno=&type=" + querytype;

        byte[] out = rawdata.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());

        BufferedReader br = new BufferedReader(
                new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));

        br.read();

        String line = br.readLine();

        System.out.println(line);

        br.close();

        http.disconnect();

        JSONObject jo = JSONObject.parseObject(line);
//        System.out.println(JSONArray.parseArray(jo.get("data")));
        System.out.println(jo.get("data"));
        JSONArray ja = (JSONArray) jo.get("data");
        for(int i = 0; i < ja.size(); ++i) {
            JSONObject curobj = ja.getJSONObject(i);
            System.out.println(curobj.get("n_cname"));
        }
        return jo;

    }


    public void crawlAll() {

    }

    public static void main(String[] args) {
        try{
            OBEUser obeu = new OBEUser("2019201409", "wangyuansenkeai");
            obeu.doLogin();
            System.out.println(obeu.Cookie);
//            obeu.crawlPagedCoursework(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
