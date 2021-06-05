package main.java.client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OBECourse {
    String CourseID;
    String CourseName;
    OBEPerson teacher;
    ArrayList<OBEPerson> ta;
    HashMap<Integer, OBEHomework> homework;
    HashMap<Integer, OBEAttachment> attachment;
    public void getPagedHomework(Map<String, String> Cookie, int pageNum) {
        try {
            String coursehwurl = "http://obe.ruc.edu.cn/index/homework/index/cno/" + CourseID + "/p/" + pageNum + ".html";
            Document document = Jsoup.connect(coursehwurl).cookies(Cookie).get();
            Elements content = document.getElementsByClass("panel panel-default");

            String homeworkFnamePat = "<input type=\"hidden\" name=\"fname\" value=\"(.*)\"> ";
            Pattern fnamePat = Pattern.compile(homeworkFnamePat);

            String homeworkFnoPat = "<input type=\"hidden\" name=\"fno\" value=\"(.*)\"> ";
            Pattern fnoPat = Pattern.compile(homeworkFnoPat);

            for(Element cur: content) {
                OBEHomework curhw = new OBEHomework();
                String[] temp = cur.id().split("-");
                curhw.id = Integer.parseInt(temp[1]);

                Element titleElem = cur.getElementById("hno-" + curhw.id);
                curhw.title = titleElem.text();

                Element ddlElem = cur.getElementById("deadline-" + curhw.id);
                curhw.deadLine = ddlElem.text();

                Elements sub = cur.getElementsByClass("time pull-right");
                for(Element s: sub) {
                    curhw.publishTime = s.text();
                    break;
                }

                sub = cur.getElementsByClass("content");
                curhw.description = "";
                for(Element s: sub) {
                    curhw.description += s.text();
                }

                sub = cur.getElementsByClass("table");
                for(Element s: sub) {
                    String attachtmp = s.toString();
                    Matcher m = fnamePat.matcher(attachtmp);
                    OBEAttachment curatt = null;
                    if(m.find()) {
                        curatt = new OBEAttachment();
                        curatt.name = m.group(1);
                    }
                    m = fnoPat.matcher(attachtmp);
                    if(m.find()) {
                        assert curatt != null;
                        curatt.id = Integer.parseInt(m.group(1));
                    }
                    if(curatt != null) {
                        curhw.attachments.add(curatt);
                    }
                }
                sub = cur.getElementsByClass("unfinished");
                for(Element s: sub) {
                    curhw.status = 1;
//                    System.out.println(s);
                }
                sub = cur.getElementsByClass("tabProduct");
                for(Element s: sub) {
                    curhw.scoring += s.text() + "\n";
                }
//                System.out.println(curhw);
//                homework.put(curhw.id, curhw);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getAllHomework(Map<String, String> Cookie) {
        try{
            String coursehwurl = "http://obe.ruc.edu.cn/index/homework/index/cno/" + CourseID + ".html";
            Document coursehwmain = Jsoup.connect(coursehwurl).cookies(Cookie).get();
            String hwmaincont = coursehwmain.body().toString();
            if(!hwmaincont.contains("老师还没布置作业诶~~走喽，出去玩儿喽~~")) {
                String pageNumStr = "(.*) 条记录 1/(.*) 页 ";
                Pattern pageNumPat = Pattern.compile(pageNumStr);
                Matcher m = pageNumPat.matcher(hwmaincont);

                if(m.find()) {
                    int recordNum = Integer.parseInt(m.group(1).strip());
                    int pageNum = Integer.parseInt(m.group(2).strip());
                    for(int i = 1; i <= pageNum; ++i) {
                        getPagedHomework(Cookie, i);
                    }
                    System.out.println("pagenum = " + recordNum + ", actual = " + homework.size());
                }
                else {
                    System.out.println("FAILED");
                }
            }
            else {

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public void getAllAttachment(Map<String, String> Cookie) {

    }
    OBECourse(String courseid, String coursename) {
        CourseID = courseid;
        CourseName = coursename;
        homework = new HashMap<>();
        attachment = new HashMap<>();
    }
}
