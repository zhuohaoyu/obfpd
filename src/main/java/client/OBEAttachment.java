package main.java.client;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OBEAttachment {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean downloadTo( Map<String,String> ck , String path ){
        return downloadDocument( ck , path , name , Integer.toString( id ) , name ) ;
    }

    private boolean downloadDocument(Map <String, String> ck, String path, String FileName, String fno, String submit) {
        try {
            String url = "http://obe.ruc.edu.cn/index/common/documentDownload.html";
            Map<String, String> data = new HashMap<>();
            System.out.println( FileName + " " + fno );
//            data.put("fname", FileName);
            data.put("fno", fno);
//            data.put("submit", submit);
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
                    .header("Referer", "http://obe.ruc.edu.cn/document/index/cno/2021rpfugnl2pxin")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.62")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .maxBodySize(100 * 1024 * 1024)
                    .data(data)
                    .cookies(ck)
                    .method(Connection.Method.POST)
                    .execute()
                    .charset("UTF-8");
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
}
