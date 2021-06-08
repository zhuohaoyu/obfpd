package main.java.client;

import main.java.App;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    ArrayList<OBEAttachment> attachments;
    // 本地数据目录
    String localPath;
    // 本地缓存目录
    String localTemp ;
    // 本地上次提交时间
    String lastUpdateTime = null ;
    // 每个文件是否被选择提交
    Map< String , Boolean > uploadSelected ;

    public String getLocalPath() {
        return localPath;
    }

    public String getLocalTemp() {
        return localTemp;
    }

    public void setStatus(int s) {
        status = s;
    }

    public void setLocalPath(String s) {
        localPath = s;
    }

    public void setLocalTemp(String localTemp) {
        this.localTemp = localTemp;
        File dir = new File( localTemp ) ;
        File lastupdFile = new File( dir , "lastUPD.txt" ) ;
        if(!lastupdFile.exists()){
            if( lastUpdateTime != null ) {
                try {
                    var osw = new OutputStreamWriter(new FileOutputStream( lastupdFile ), StandardCharsets.UTF_8);
                    osw.write( lastUpdateTime );
                    osw.close();
                } catch ( Exception ae ){ ae.printStackTrace(); }
            }
        } else {
            try {
                var fin = new BufferedReader(new FileReader(lastupdFile ) );
                lastUpdateTime = fin.readLine() ;
            }catch ( Exception ae ){
                System.err.println( "in setLocalTemp: read existing tempfile lastUPD.txt error");
            }
        }

        File chooseStatusFile = new File( dir , "chosedFileList.txt" ) ;
        if(!lastupdFile.exists()){
            writeInChosedFileList( uploadSelected );
        } else {
            try {
                var fin = new BufferedReader(new FileReader(chooseStatusFile ) );
                String file;
                while( (file = fin.readLine()) != null ){
//                    System.out.println( title + ": load " + file ) ;
                    uploadSelected.put( file , true ) ;
                }
            }catch ( Exception ae ){
                writeInChosedFileList ( uploadSelected ) ;
            }
        }
    }

    public void writeInChosedFileList( Map<String ,Boolean> mp ){
        this.uploadSelected = mp ;
        try {
            File dir = new File(localTemp);
            File chooseStatusFile = new File( dir , "chosedFileList.txt" ) ;
            var osw = new OutputStreamWriter(new FileOutputStream( chooseStatusFile ), StandardCharsets.UTF_8);
            if( mp != null ) {
                for (var item : mp.keySet()) {
                    if (mp.get(item)) {
                        osw.write(item);
                        osw.write("\n");
                    }
                }
            }
            osw.close();
        } catch ( Exception ae ){ ae.printStackTrace(); }
    }

    public void writeInUPDTimeLog(){
        lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) ;
        try {
            File dir = new File(localTemp);
            File lastupdFile = new File(dir, "lastUPD.txt");
            var osw = new OutputStreamWriter(new FileOutputStream( lastupdFile ), StandardCharsets.UTF_8);
            osw.write( lastUpdateTime );
            osw.close();
        } catch ( Exception ae ){ ae.printStackTrace(); }
    }

    public Map<String, Boolean> getUploadSelected() { return uploadSelected; }

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
        uploadSelected = new HashMap<String,Boolean>() ;
        status = 0;
    }

    public long checkLeftDay() {
        if( status == 0 ) return -999 ;
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(deadLine);
        } catch (ParseException e) {
            System.err.println( "in checkLeftDay : parse error") ;
        }
        Calendar ddl = Calendar.getInstance();
        ddl.setTime(date);
        Calendar now = Calendar.getInstance() ;

        long aTime=now.getTimeInMillis() , bTime = ddl.getTimeInMillis() ;
//        if( aTime >= bTime ) return -1 ;
//        System.out.printf( "single check Day: %d %d\n" , bTime - aTime , ( ( bTime - aTime ) / 1000 / 60 / 60 + 23 ) / 24  );
        return ( ( bTime - aTime ) / 1000 / 60 / 60 + 23 ) / 24 ;
  ;
    }

    public Boolean checkFileUpdate(){
        if( lastUpdateTime == null ) return false ;
        String path = localPath ;
        System.out.println( "checking Update... " + path);
        File file = new File(path);
        File[] tempList = file.listFiles();
        Calendar lsupd = Calendar.getInstance() ;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( lastUpdateTime ) ;
            lsupd.setTime( date );
        } catch ( ParseException e ){
            System.err.println( "in checkFileUpdate : parse error") ;
        }

        for (int i = 0; i < tempList.length; i++) {
            if( uploadSelected.get( tempList[i].getName() ) == null ) continue ;
            if( !uploadSelected.get( tempList[i].getName() ) ) continue ;
            System.out.println( "    " + tempList[i].getName());
            Date date = new Date(tempList[i].lastModified());
            Calendar lsm = Calendar.getInstance() ;
            lsm.setTime( date );
            if( lsupd.getTimeInMillis() < lsm.getTimeInMillis() )
                return true ;
        }
        return false ;
    }

    public String toString() {
        String ret;
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
            FileOutputStream out = (new FileOutputStream(path + FileName));
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
