package main.java.client;

import com.formdev.flatlaf.*;
import org.jsoup.*;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OBELoginWorker implements OBEConstants{
    public Map<String,String> getCookie(String usernumber, String password) throws IOException {
        Map<String,String> data = new HashMap<>();
        data.put("usernumber", usernumber);
        data.put("password", password);

        Connection.Response login = Jsoup.connect(OBE_LOGIN_URL)
                .ignoreContentType(true) // 忽略类型验证
                .followRedirects(false) // 禁止重定向
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
}
