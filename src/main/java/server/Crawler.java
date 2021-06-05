package main.java.server;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Crawler implements Runnable {
    private int SleepTime;
    private DataBase db;
    private Map <String, String> cookie;
    public Crawler(int __SleepTime) {
        db = new DataBase(":data:test.db");
        SleepTime = __SleepTime;
    }
    public boolean getCourses(Map <String, String> ck) {
        try {
            FileOutputStream fos = new FileOutputStream("./data/course.txt");
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

                db.addCourse(CourseID, CourseName, CourseTeacher);
                osw.write(CourseID + "\t" + CourseName + "\t" + CourseTeacher + "\t" + CourseUrl + "\n");
            }
            osw.close();
            return true;
        }
        catch (IOException e) {
            System.err.println("IOException: " + e);
            return false;
        }
    }
    public boolean getHomeworks(Map <String, String> ck, String CourseID) {
        try {
            String url = "http://obe.ruc.edu.cn/index/homework/index/cno/" + CourseID + "/p/0.html";
            FileOutputStream fos = new FileOutputStream("./data/homeworks/" + CourseID + ".txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
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
                } catch (Exception e) {
                    break;
                }
                for (Element hw : content) {
                    Elements detail = hw.getElementsByClass("accordion-toggle title homework-title");
                    String HomeworkName = null;
                    String HomeworkDDL = null;
                    String HomeworkST = null;
                    String HomeworkHno = null;
                    String HomeworkContent = null;
                    String HomeworkAtt = null;
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

                    detail = hw.getElementsByClass("table");
                    for (Element at : detail) {
                        String[] buf = at.toString().split("value=\"");
                        if (buf.length < 4) continue;
                        String fname = buf[1].split("\"")[0];
                        String fno = buf[2].split("\"")[0];
                        String submit = buf[3].split("\"")[0];
                        HomeworkAtt = fname + "," + fno + "," + submit;
                    } // get homework attachment

                    if (HomeworkName != null && HomeworkDDL != null) {
                        osw.write(HomeworkHno + "\t" + HomeworkName + "\t" + HomeworkST + "\t" + HomeworkDDL + "\t" + HomeworkContent + "\t" + HomeworkAtt + "\n");
                    }
                }
            }
            osw.close();
            return true;
        }
        catch (IOException e) {
            System.err.println("IOException: " + e);
            return false;
        }
    }
    public boolean crawlHomeworks(Map <String, String> ck, String CourseID) {
        Set<String> homework = new HashSet<String>();
        List<String> oldhw = db.getCourseHomework(CourseID);
        for (String old : oldhw) {
            homework.add(old);
        }
        try {
            String url = "http://obe.ruc.edu.cn/index/homework/index/cno/" + CourseID + "/p/0.html";
            FileOutputStream fos = new FileOutputStream("./data/homeworks/" + CourseID + ".txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
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
                } catch (Exception e) {
                    break;
                }
                for (Element hw : content) {
                    Elements detail = hw.getElementsByClass("accordion-toggle title homework-title");
                    String HomeworkName = "";
                    String HomeworkDDL = "";
                    String HomeworkST = "";
                    String HomeworkHno = "";
                    String HomeworkContent = "";
                    String HomeworkAtt = "";
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

                    detail = hw.getElementsByClass("table");
                    for (Element at : detail) {
                        String[] buf = at.toString().split("value=\"");
                        if (buf.length < 4) continue;
                        String fname = buf[1].split("\"")[0];
                        String fno = buf[2].split("\"")[0];
                        String submit = buf[3].split("\"")[0];
                        HomeworkAtt = fname + "," + fno + "," + submit;
                    } // get homework attachment

                    if (HomeworkName != "" && HomeworkDDL != "") {
                        if (homework.contains(HomeworkHno)) {
                            homework.remove(HomeworkHno);
                        }
                        db.updateHomework(HomeworkHno, HomeworkName, HomeworkST, HomeworkDDL, HomeworkContent, HomeworkAtt, CourseID);
                        osw.write(HomeworkHno + "\t" + HomeworkName + "\t" + HomeworkST + "\t" + HomeworkDDL + "\t" + HomeworkContent + "\t" + HomeworkAtt + "\n");
                    }
                }
            }
            for (String old : homework) {
                System.err.println("Delete homework: " + old);
                db.deleteHomework(old, CourseID);
            }
            osw.close();
            return true;
        }
        catch (Exception e) {
            System.err.println("Exception: " + e);
            return false;
        }
    }
    public boolean downloadDocument(Map <String, String> ck, String path, String FileName, String fno, String submit) {
        try {
            String url = "http://obe.ruc.edu.cn/index/common/documentDownload.html";
            Map<String, String> data = new HashMap<>();
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
    public void getDocuments(Map <String, String> ck, String CourseID, String filepath) {
        String url = "http://obe.ruc.edu.cn/index/document/index/cno/" + CourseID + "/p/0.html";
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
            } catch (Exception e) { break; }
            String fname = null;
            String fno = null;
            String submit = null;
            content = document.getElementsByClass("table table-striped");
            for (Element dc : content) {
                for (String detail : dc.toString().split("<tr>")) {
                    String[] buf = detail.split("value=\"");
                    if (buf.length < 4) continue;
                    fname = buf[1].split("\"")[0];
                    fno = buf[2].split("\"")[0];
                    submit = buf[3].split("\"")[0];
                    boolean tmp = downloadDocument(ck, filepath, fname, fno, submit);
                    if (tmp == false)
                        tmp = downloadDocument(ck, filepath, fname, fno, submit);
                    if (tmp == false) {
                        System.err.println("Download " + fname + " fail.");
                    }
                }
            }
        }
    }
    public boolean reCrawl(Map <String, String> ck) {
        try {
            FileOutputStream fos = new FileOutputStream("./data/course.txt");
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
                db.addCourse(CourseID, CourseName, CourseTeacher);
                crawlHomeworks(ck, CourseID);
                osw.write(CourseID + "\t" + CourseName + "\t" + CourseTeacher + "\t" + CourseUrl + "\n");
            }
            osw.close();
            return true;
        }
        catch (IOException e) {
            System.err.println("IOException: " + e);
            return false;
        }
    }

    @Override
    public void run() {
        Login login = new Login();
        boolean flag = true;
        String line = null;
        Map <String, String> ck = null;
        Account account = new Account();
        for (String usernumber : account.student.keySet()) {
            flag = false;
            try {
                String password = account.student.get(usernumber);
                ck = login.getCookie(usernumber, password);
            }
            catch (IOException e) {
                flag = true;
            }
            if (!flag) break;
        }
        while (true) {
            this.reCrawl(ck);
            try {
                Thread.sleep(SleepTime);
            }
            catch (Exception e) { }
        }
    }

    static public void main(String[] argv) {
        new Thread(new Crawler(5 * 60 * 1000)).run();
    }
}

