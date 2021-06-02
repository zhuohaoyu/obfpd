import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

class CrawleLogin {
    public void setCookies(String url) throws IOException {
        String cok = "uid=k59qlv8cejhrm9vafn0g3hhqo0; Hm_lvt_837af77cdd844632ad02ef10fb7851f0=1622532625,1622534634,1622545059,1622603458; Hm_lvt_6c8c3ce0743dd12cf03043954fddd040=1622532625,1622534634,1622545059,1622603458; Hm_lpvt_6c8c3ce0743dd12cf03043954fddd040=1622608774; Hm_lpvt_837af77cdd844632ad02ef10fb7851f0=1622608774";

        Document document = Jsoup.connect(url)
                .header("Cookie", cok)
                .get();
        if (document != null) {
            String test = document.body().text();
            /*Element te = document.body();
            Elements content = document.getElementsByClass("dropdown-menu");
            for (Element detail : content) {
                System.out.println("class name: " + detail.className());
                System.out.println(content.select("a[href]"));
                System.out.println(detail);
                break;
            }*/
            test = document.title();
            System.out.println("title: " + test);
            /*Element element = document.select("title").first();
            if (element == null) {
                System.out.println("没有找到 .info h1 标签");
                return;
            }
            String userName = element.ownText();
            System.out.println("test obe: " + userName);
             */
        } else {
            System.out.println("出错啦！！！！！");
        }
    }
}

class Login {
    public Map<String,String> getCookie(String usernumber, String password) throws IOException {
        Map<String,String> data = new HashMap<>();
        data.put("usernumber", usernumber);
        data.put("password", password);
        Connection.Response login = Jsoup.connect("http://obe.ruc.edu.cn/index/login/login.html")
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

class test {
    public static void main(String[] args) throws Exception {
        String user_info_url = "http://obe.ruc.edu.cn/index/home/index.html";
        Login lg = new Login();
        Map <String, String> c1 = lg.getCookie("2019201408", "algorithm");
        Map <String, String> c2 = lg.getCookie("2019201409", "keaiwangyuansen");
        Map <String, String> c3 = lg.getCookie("2019201420", "******");
        lg.test(c1, user_info_url);
        lg.test(c2, user_info_url);
        lg.test(c3, user_info_url);
    }
}