package main.java.server;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Login {
    public Map<String,String> getCookie(String usernumber, String password) throws IOException {
        Map<String,String> data = new HashMap<>();
        data.put("usernumber", usernumber);
        data.put("password", password);
        Connection.Response login = Jsoup.connect("http://obe.ruc.edu.cn/index/login/login.html")
                .ignoreContentType(true)
                .followRedirects(false)
                .postDataCharset("utf-8")
                .header("Upgrade-Insecure-Requests","1")
                .header("Accept","*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,mt;q=0.6")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                .header("Host", "obe.ruc.edu.cn")
                .header("Origin", "http://obe.ruc.edu.cn")
                .header("Referer", "http://obe.ruc.edu.cn/index/index/index.html")
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.62")
                .header("X-Requested-With","XMLHttpRequest")
                .data(data)
                .method(Connection.Method.POST)
                .execute()
                .charset("UTF-8");
        return login.cookies();
    }
    public void test(Map <String, String> cok, String userInfoUrl) throws IOException {
        System.out.println(cok);
        Document document = Jsoup.connect(userInfoUrl)
                .cookies(cok)
                .get();
        if (document != null) {
            String test = document.title();
            System.out.println("title: " + test);
            Elements content = document.getElementsByClass("dropdown-menu");
            for (Element detail : content) {
                System.out.println(content.select("a[href]"));
                break;
            }
        }
    }
}

