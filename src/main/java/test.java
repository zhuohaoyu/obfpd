package main.java ;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class test{
    static public Map<String,String> getCookie(String usernumber, String password) throws IOException {
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
        String url = "http://obe.ruc.edu.cn/index/course/index.html";
        Document document = Jsoup.connect(url).cookies(login.cookies()).get();
        System.out.println(document.title());
        return login.cookies();
    }
    static public boolean downloadHomeworkAttachment(Map <String, String> ck, String path, String FileName, String fno, String submit) {
        try {
//            String url = "http://obe.ruc.edu.cn/index/common/documentDownload.html";
            String url = "http://obe.ruc.edu.cn/index/course/index.html";
            Document document = Jsoup.connect(url).cookies(ck).get();
            System.out.println("test1" + document.title());
            Map<String, String> data = new HashMap<>();
            data.put("fname", "g2");
            data.put("fno", "5415");
            data.put("submit", "g2");
            Connection.Response content = Jsoup.connect("http://obe.ruc.edu.cn/index/common/documentDownload.html")
                    .timeout(60000)
                    .ignoreContentType(true)
                    .postDataCharset("utf-8")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,mt;q=0.6")
                    .header("Connection", "keep-alive")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Host", "obe.ruc.edu.cn")
                    .header("Origin", "http://obe.ruc.edu.cn")
                    .header("Referer", "http://obe.ruc.edu.cn/index/document/index/cno/20211y4anes9e2xi.html")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.62")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .maxBodySize(100 * 1024 * 1024)
                    .data(data)
                    .cookies(ck)
                    .method(Connection.Method.POST)
                    .execute()
                    .charset("UTF-8");
            System.out.println(content.body());
            FileOutputStream out = (new FileOutputStream(new java.io.File(path + FileName)));
            out.write(content.bodyAsBytes());
            out.close();
            return true;
        }
        catch (IOException e) {
            System.err.println("IOException: " + e);
            return false;
        }
    }
    public static void main(String[] args) throws IOException {
        Map<String,String> mp = new HashMap<String,String>() ;
        mp = getCookie("2019201420", "******");
        String tpath = "F:\\我的大学\\2021春\\Java\\作业\\大作业\\OBFPDdata\\2019201420\\2020-2021（2）数据科学导论（覃雄派班）\\PageRank算法实现\\" ;
        String filename = "DS_lab_PageRank.pdf" ;
        String fno = "1409" ;
        downloadHomeworkAttachment(mp, tpath, filename, fno, filename) ;
    }
}
