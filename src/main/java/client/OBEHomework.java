package main.java.client;

import java.util.ArrayList;

public class OBEHomework {
    String publishTime, deadLine;
    String title;
    String description;
    String scoring;
    int status;
    int id;
    ArrayList<OBEAttachment> attachments;
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
