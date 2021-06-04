package main.java.server;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Crawler {
    public Map <String, String> getCourses(Map <String, String> ck) throws IOException {
        FileOutputStream fos = new FileOutputStream("course.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        String url = "http://obe.ruc.edu.cn/index/course/index.html";
        Document document = Jsoup.connect(url).cookies(ck).get();
        Elements content = document.getElementsByClass("thumbnail col-lg-2 col-md-2 col-sm-2 col-xs-2 block1 ellipsis");
        for (Element item : content.select("a[href]")) {
            String detail = item.toString();
            String[] buf = detail.split("\"");
            String CourseUrl = "http://obe.ruc.edu.cn" + buf[1];
            String CourseName = buf[2].split(">")[1].split("<")[0];
            String CourseTeacher = buf[2].split(">")[3].split("<")[0];
            String CourseID = buf[1].split(".html")[0].split("/")[5];
            osw.write(CourseID + "\t" + CourseName + "\t" + CourseTeacher + "\t" + CourseUrl + "\n");
        }
        osw.close();
        return ck;
    }
    public Map <String, String> getHomeworks(Map <String, String> ck, String url) throws IOException {
        FileOutputStream fos = new FileOutputStream("homework.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        url = url.replace("notice", "homework");
        url = url.replace(".html", "/p/0.html");
        Map<String, String> isVis = new HashMap<String, String>();
        int id = 0;
        while (true) {
            Document document = null;
            Elements content = null;
            try {
                id += 1;
                url = url.replace("/p/" + Integer.toString(id - 1) + ".html", "/p/" + Integer.toString(id) + ".html");
                document = Jsoup.connect(url).cookies(ck).get();
                content = document.getElementsByClass("panel panel-default");
                for (Element page : content) {
                    String buf = page.toString();
                    if (isVis.containsKey(buf)) throw new Exception("visit same page.");
                    else isVis.put(buf, "Exist");
                }
            }
            catch (Exception e) { break; }
            for (Element hw : content) {
                Elements detail = hw.getElementsByClass("accordion-toggle title homework-title");
                String HomeworkName = null;
                String HomeworkDDL = null;
                String HomeworkST = null;
                String HomeworkHno = null;
                String HomeworkContent = null;
                for (Element hn : detail) {
                    HomeworkName = hn.toString().split(">")[1].split("<")[0];
                } // get homework name

                detail = hw.getElementsByClass("panel-body out collapse");
                for (Element ddl : detail) {
                    String[] buf = ddl.toString().split("<span id=\"deadline-");
                    buf = buf[1].split("<");
                    buf = buf[0].split(">");
                    HomeworkDDL = buf[1];
                } // get homework deadline

                detail = hw.getElementsByClass("time pull-right");
                for (Element st : detail) {
                    HomeworkST = st.toString().split("</small>")[0].split(">")[1];
                } // get homework start time

                detail = hw.getElementsByClass("accordion-toggle title homework-title");
                for (Element st : detail) {
                    HomeworkHno = st.toString().split("id=\"hno-")[1].split("\"")[0];
                } // get homework hno

                detail = hw.getElementsByClass("content");
                for (Element ct : detail) {
                    String[] buf = ct.toString().split("</p></span>");
                    buf = buf[0].split("<span class=\"content\"><p>");
                    if (buf.length > 1) {
                        HomeworkContent = buf[1];
                        while (true) {
                            int l = HomeworkContent.indexOf('<');
                            int r = HomeworkContent.indexOf('>');
                            if (l == -1 || r == -1) break;
                            HomeworkContent = HomeworkContent.substring(0, l) + HomeworkContent.substring(r + 1, HomeworkContent.length());
                        }
                    }
                } // get homework content

                if (HomeworkName != null && HomeworkDDL != null)
                    osw.write(HomeworkHno + "\t" + HomeworkName + "\t" + HomeworkST + "\t" + HomeworkDDL + "\t" + HomeworkContent + "\n");
            }
        }
        osw.close();
        return ck;
    }
    private void downloadDocument(Map <String, String> ck, String path, String FileName, String fno, String submit) throws IOException {
        String url = "http://obe.ruc.edu.cn/index/common/documentDownload.html";
        Map<String,String> data = new HashMap<>();
        data.put("fname", FileName);
        data.put("fno", fno);
        data.put("submit", submit);
        Connection.Response content = Jsoup.connect("http://obe.ruc.edu.cn/index/common/documentDownload.html")
                .timeout(60000)
                .ignoreContentType(true)
                .postDataCharset("utf-8")
                .header("Upgrade-Insecure-Requests","1")
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,mt;q=0.6")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/x-www-form-urlencoded")
                .header("Host", "obe.ruc.edu.cn")
                .header("Origin", "http://obe.ruc.edu.cn")
                .header("Referer", "http://obe.ruc.edu.cn/document/index/cno/2021rpfugnl2pxin")
                .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.62")
                .header("X-Requested-With","XMLHttpRequest")
                .maxBodySize(100*1024*1024)
                .data(data)
                .cookies(ck)
                .method(Connection.Method.POST)
                .execute()
                .charset("UTF-8");
        FileOutputStream out = (new FileOutputStream(new java.io.File(path + FileName)));
        out.write(content.bodyAsBytes());
        out.close();
    }
    public void getDocuments(Map <String, String> ck, String url, String filepath) throws IOException {
        url = url.replace("notice", "document");
        url = url.replace(".html", "/p/0.html");
        Map<String, String> isVis = new HashMap<String, String>();
        int id = 0;
        while (true) {
            Document document = null;
            Elements content = null;
            try {
                id += 1;
                url = url.replace("/p/" + Integer.toString(id - 1) + ".html", "/p/" + Integer.toString(id) + ".html");
                document = Jsoup.connect(url).cookies(ck).get();
                Elements tmp = document.getElementsByClass("page");
                for (Element page : tmp) {
                    String buf = page.toString();
                    if (isVis.containsKey(buf)) throw new Exception("visit same page.");
                    else isVis.put(buf, "Exist");
                }
            }
            catch (Exception e) { break; }
            String fname = null;
            String fno = null;
            String submit = null;
            content = document.getElementsByClass("table table-striped");
            for (Element dc : content) {
                for (String detail : dc.toString().split("<tr>")) {
                    String[] buf = detail.split("value=\"");
                    if (buf.length < 4) continue;
                    fname  = buf[1].split("\"")[0];
                    fno    = buf[2].split("\"")[0];
                    submit = buf[3].split("\"")[0];
                    downloadDocument(ck, filepath, fname, fno, submit);
                }
            }
        }
    }
    /*
    static public void main(String[] argv) {
        Login login = new Login();
        String usernumber = "2019201408";
        String password = "algorithm";
        Map <String, String> ck = null;
        try {
            ck = login.getCookie(usernumber, password);
//            new Crawler().getCourses(ck);
//            String CourseUrl = "http://obe.ruc.edu.cn/index/notice/index/cno/20213ban15xk2vdv.html";
//            String CourseUrl = "http://obe.ruc.edu.cn/index/notice/index/cno/20210s130b7kyx3c.html";
//            String CourseUrl = "http://obe.ruc.edu.cn/index/notice/index/cno/2021bqjnnbmffiqn.html";
//            String CourseUrl = "http://obe.ruc.edu.cn/index/notice/index/cno/20212ache8wmuykt.html";
//            new Crawler().getHomeworks(ck, CourseUrl);
//            new Crawler().getDocuments(ck, CourseUrl, "./testdownload/");
        }
        catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }
    */
}

