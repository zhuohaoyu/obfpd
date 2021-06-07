package main.java.client;

import main.java.App;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OBEHomework {
    // 发布时间、ddl
    String publishTime, deadLine;
    // 作业标题
    String title;
    // 作业描述，HTML格式
    String description;
    // （部分作业有）评分标准、分数
    String scoring;
    // 是否待做，为1表示尚未提交
    int status;
    // 作业编号
    int id;
    // 所有附件
    String localPath;
    // 本地数据目录
    ArrayList<OBEAttachment> attachments;

    public String getLocalPath() {
        return localPath;
    }

    public void setStatus(int s) {
        status = s;
    }

    public void setLocalPath(String s) {
        localPath = s;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getScoring() {
        return scoring;
    }

    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public ArrayList<OBEAttachment> getAttachments() {
        return attachments;
    }
    public OBEHomework() {
        scoring = "";
        attachments = new ArrayList<>();
        status = 0;
    }
    public String toString() {
        String ret = "";
        if(status == 1) ret = "[Unfinished]";
        else ret = "[Finished]";
        ret += "Homework: " + title + "\n";
        ret += publishTime + ", " + deadLine + "\n";
        ret += description + "\n";
        ret += "ID: " + id + "\n";
        ret += "Scoring: " + scoring + "\n";
        ret += "Attachments:";
        for(OBEAttachment att: attachments) {
            ret += "(" + att.name + "," + att.id + ")";
        }

        ret += '\n';
        return ret;
    }

    public boolean downloadHomeworkAttachment(Map<String, String> ck, String path, String FileName, String fno, String submit) {
        System.out.println( "enter attachment download" + ck.toString() +  path + FileName + fno + submit ) ;
        try {
            Map<String, String> data = new HashMap<>();
            data.put("fname", FileName);
            data.put("fno", fno);
            data.put("submit", submit);
            Connection.Response content = Jsoup.connect("http://obe.ruc.edu.cn/index/common/homeworkDownload.html")
                    .timeout(5000)
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
        catch (Exception e) {
            System.err.println("download homework attachment Exception: " + e);
            return false;
        }
    }
}
