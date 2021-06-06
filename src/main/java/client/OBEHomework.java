package main.java.client;

import java.util.ArrayList;

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
}
