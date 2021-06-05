package main.java.server;

import java.util.HashMap;
import java.util.Map;

public class Account {
    Map<String, String> student;
    public Account() {
        student = new HashMap<String, String>();
        student.put("2019201408", "algorithm");
        student.put("2019201409", "keaiwangyuansen");
        student.put("2019201420", "******");
    }
}
