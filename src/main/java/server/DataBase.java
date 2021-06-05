package main.java.server;

import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class DataBase {
    Connection c = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public static void main(String[] args) {
        /*
        瞎测的，差不多都work
        var db = new DataBase(???);
        db.initCourseTable();
        db.addCourse(db.courseMap("01", "class1", "fpd"));
        db.addCourse(db.courseMap("02", "class2", "fpd"));
        db.addCourse(db.courseMap("03", "ass3", "pdf"));
        var res = db.queryCourse(db.queryCourseMap("as", "pdf"));
        System.out.println("Query Succeeded");
        for(var x: res) System.out.println(x);
        db.updateHomework("1001", "class1的作业1", "2021-06-04 20:11", "2021-06-04 20:20", "略", "", "01");
        db.updateHomework("1002", "class1的作业2", "2021-06-04 21:10", "2021-06-04 21:20", "略", "", "01");
        var res = db.getCourseHomework("01");
        System.out.println("Query Succeeded");
        for(var x: res) System.out.println(x);
        var res = db.queryDDLHomework("01", "2021-06-04 20:21", "9999-12-31 23:59");
        System.out.println("Query Succeeded");
        for(var x: res) System.out.println(x);
        db.close();
        */
    }
 
    /* @brief   内部函数 通过课程ID获取课程名
     */
    public String getCourseName(String courseID) throws SQLException {
        pst = c.prepareStatement("SELECT name FROM COURSES WHERE id = ?");
        pst.setString(1, courseID);
        rs = pst.executeQuery();
        rs.next();
        String courseName = rs.getString("name");
        rs.close();
        pst.close();
        return courseName;
    }

    /* @brief   内部函数 通过作业ID获取作业名
     */
    public String getHomeworkName(String homeworkID, String courseID) throws SQLException {
        pst = c.prepareStatement("SELECT name FROM HOMEWORKS WHERE course_id = ? AND id = ?");
        pst.setString(1, courseID);
        pst.setString(2, homeworkID);
        rs = pst.executeQuery();
        rs.next();
        String homeworkName = rs.getString("name");
        rs.close();
        pst.close();
        return homeworkName;
    }

    /* @brief  获取 "yyyy-MM-dd HH:mm:ss" 格式的当前时间
     */
    public static String getCurTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }

    /* @brief   生成一个课程对应的Map
     * @param   见addCourse方法
     */
    public Map<String, String> courseMap(String id, String name, String teacher) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("id", id);
        retMap.put("name", name);
        retMap.put("teacher", teacher);
        return retMap;
    }

    /* @brief   生成一个作业对应的Map
     * @param   见updateHomework方法
     */
    public Map<String, String> homeworkMap(String id, String name, String timePull,
                                           String timeDDL, String description,
                                           String attachment, String courseID) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("id", id);
        retMap.put("name", name);
        retMap.put("time_pull", timePull);
        retMap.put("time_ddl", timeDDL);
        retMap.put("description", description);
        retMap.put("attachment", attachment);
        retMap.put("course_id", courseID);
        return retMap;
    }

    /* @brief   生成一个课程查询对应的Map
     * @param   见queryCourse方法
     */
    public Map<String, String> queryCourseMap(String name, String teacher) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("name", name);
        retMap.put("teacher", teacher);
        return retMap;
    }

    /* @brief   创建项目所需的所有表
     */
    public void initCourseTable() {
        try {
            String sql;
            /* sqlite 不支持 if not exists */
            st = c.createStatement();
            st.executeUpdate("PRAGMA foreign_keys = ON");
            
            sql = "CREATE TABLE COURSES ("
                + "    id              CHAR(16)    PRIMARY KEY     NOT NULL,"
                + "    name            TEXT        NOT NULL,"
                + "    teacher         TEXT        NOT NULL"
                + ")";
            st.executeUpdate(sql);
            System.out.println("Created table \"COURSES\" successfully");

            sql = "CREATE TABLE HOMEWORKS ("
                + "    id              INTEGER     PRIMARY KEY     NOT NULL,"
                + "    name            TEXT        NOT NULL,"
                + "    time_pull       TEXT        NOT NULL,"
                + "    time_ddl        TEXT        NOT NULL,"
                + "    description     TEXT,"
                + "    attachment      TEXT,"
                + "    course_id       CHAR(16)    NOT NULL,"
                + "    FOREIGN KEY (course_id) REFERENCES COURSES(id) ON UPDATE CASCADE"
                + ")";
            st.executeUpdate(sql);
            sql = "CREATE INDEX course_index on HOMEWORKS(course_id)";
            st.executeUpdate(sql);
            System.out.println("Created table \"HOMEWORKS\" successfully");
            
            sql = "CREATE TABLE UPDATES ("
                + "    time            TEXT        NOT NULL,"
                + "    course_name     TEXT        NOT NULL,"
                + "    homework_name   TEXT        NOT NULL,"
                + "    type            TEXT        NOT NULL,"
                + "    old             TEXT,"
                + "    new             TEXT"
                + ")";
            st.executeUpdate(sql);
            sql = "CREATE INDEX time_index on UPDATES(time)";
            st.executeUpdate(sql);
            System.out.println("Created table \"UPDATES\" successfully");

            sql = "CREATE TABLE POSTS ("
                + "    id              INTEGER     PRIMARY KEY     NOT NULL,"
                + "    time            TEXT        NOT NULL,"
                + "    title           TEXT        NOT NULL,"
                + "    course_id       CHAR(16)    NOT NULL,"
                + "    FOREIGN KEY (course_id) REFERENCES COURSES(id) ON UPDATE CASCADE"
                + ")";
            st.executeUpdate(sql);
            sql = "CREATE INDEX course_index2 on POSTS(course_id)";
            st.executeUpdate(sql);
            System.out.println("Created table \"POSTS\" successfully");
            
            sql = "CREATE TABLE REPLIES ("
                + "    id              INTEGER     PRIMARY KEY     NOT NULL,"
                + "    time            TEXT        NOT NULL,"
                + "    content         TEXT        NOT NULL,"
                + "    post_id         INTEGER     NOT NULL,"
                + "    FOREIGN KEY (post_id) REFERENCES POSTS(id) ON DELETE CASCADE"
                + ")";
            st.executeUpdate(sql);
            sql = "CREATE INDEX post_index on REPLIES(post_id)";
            st.executeUpdate(sql);
            System.out.println("Created table \"REPLIES\" successfully");

            st.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /* @brief   添加一个课程
     * @param   一个Map: key, value即课程信息
     *          键值必须包含:
     *            课程编号  "id"
     *            课程名    "name"
     *            教师名    "teacher"
     */
    public void addCourse(Map<String, String> course) {
        /*
         * 这里没有判断课程是否不存在。
         * 爬页面的时候记录一下上次找到的最新课程位置。
         * 他保证课程创建时间从新到老。
         */
        try {
            pst = c.prepareStatement("SELECT * FROM COURSES WHERE id = ? ");
            pst.setString(1, course.get("id"));
            rs = pst.executeQuery();
            if(rs.next()) throw new SQLSyntaxErrorException("Course already exists" + course.get("name"));
            pst.close();

            pst = c.prepareStatement("INSERT INTO COURSES VALUES (?, ?, ?)");
            pst.setString(1, course.get("id"));
            pst.setString(2, course.get("name"));
            pst.setString(3, course.get("teacher"));
            pst.executeUpdate();
            pst.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
    }
    public void addCourse(String id, String name, String teacher) {
        addCourse(courseMap(id, name, teacher));
    }

    /* @brief   更新一条作业
     * @param   一个Map: key, value即作业信息
     *          键值必须包含:
     *            作业编号 "id"
     *            作业名称 "name"
     *            课程编号 "course_id"
     *            下发时间 "time_pull"
     *            截止时间 "time_ddl"
     *            作业描述 "description"
     *            附件链接 "attachment"
     *          空键值请传入空串
     */
    public void updateHomework(Map<String, String> homework) {
        try {
            String sql;
            sql = "SELECT * FROM HOMEWORKS "
                    + "WHERE    course_id = ? "
                    + "AND      id = ? ";
            pst = c.prepareStatement(sql);
            pst.setString(1, homework.get("course_id"));
            pst.setString(2, homework.get("id"));
            rs = pst.executeQuery();
            
            // TX_BEGIN
            st = c.createStatement();
            st.executeUpdate("BEGIN");
            st.close();

            if(rs.next()) {
                ArrayList<String> colList = new ArrayList<>();
                ArrayList<String> oldList = new ArrayList<>();
                ArrayList<String> newList = new ArrayList<>();

                for(var entry: homework.entrySet())
                    if(!entry.getValue().equals(rs.getString(entry.getKey()))) {
                        colList.add(entry.getKey());
                        oldList.add(rs.getString(entry.getKey()));
                        newList.add(entry.getValue());
                    }
                pst.close();

                if (!colList.isEmpty()) {
                    sql = "UPDATE HOMEWORKS SET "
                            + "name         = ?,"
                            + "time_pull    = ?,"
                            + "time_ddl     = ?,"
                            + "description  = ?,"
                            + "attachment   = ? "
                            + "WHERE    course_id = ? "
                            + "AND      id = ? ";
                    pst = c.prepareStatement(sql);
                    pst.setString(1, homework.get("name"));
                    pst.setString(2, homework.get("time_pull"));
                    pst.setString(3, homework.get("time_ddl"));
                    pst.setString(4, homework.get("description"));
                    pst.setString(5, homework.get("attachment"));
                    pst.setString(6, homework.get("course_id"));
                    pst.setString(7, homework.get("id"));
                    pst.executeUpdate();
                    pst.close();
                    
                    /* update to MessageQueue */
                    String courseName = getCourseName(homework.get("course_id"));
                    
                    for(int i = 0; i < colList.size(); i += 1) {
                        pst = c.prepareStatement("INSERT INTO UPDATES VALUES (?, ?, ?, ?, ?, ?)");
                        pst.setString(1, getCurTime());
                        pst.setString(2, courseName);
                        pst.setString(3, homework.get("name"));
                        pst.setString(4, colList.get(i));
                        pst.setString(5, oldList.get(i));
                        pst.setString(6, newList.get(i));
                        pst.executeUpdate();
                        pst.close();
                    }
                }
            } else {
                pst.close();
                pst = c.prepareStatement("INSERT INTO HOMEWORKS VALUES (?, ?, ?, ?, ?, ?, ?)");
                pst.setString(1, homework.get("id"));
                pst.setString(2, homework.get("name"));
                pst.setString(3, homework.get("time_pull"));
                pst.setString(4, homework.get("time_ddl"));
                pst.setString(5, homework.get("description"));
                pst.setString(6, homework.get("attachment"));
                pst.setString(7, homework.get("course_id"));
                pst.executeUpdate();
                pst.close();
                
                String courseName = getCourseName(homework.get("course_id"));
                pst = c.prepareStatement("INSERT INTO UPDATES VALUES (?, ?, ?, ?, ?, ?)");
                pst.setString(1, getCurTime());
                pst.setString(2, courseName);
                pst.setString(3, homework.get("name"));
                pst.setString(4, "new");
                pst.setString(5, "");
                pst.setString(6, "");
                pst.executeUpdate();
                pst.close();
            }

            // TX_END            
            st = c.createStatement();
            st.executeUpdate("COMMIT");
            st.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
    }
    public void updateHomework(String id, String name, String timePull,
                               String timeDDL, String description,
                               String attachment, String courseID) {
        updateHomework(homeworkMap(id, name, timePull, timeDDL, description, attachment, courseID));
    }

    /* @brief   删除一条作业
     */
    public void deleteHomework(String homeworkID, String courseID) {
        try {
            // TX_BEGIN
            st = c.createStatement();
            st.executeUpdate("BEGIN");
            st.close();

            String homeworkName = getHomeworkName(homeworkID, courseID);

            String sql;
            sql = "DELETE FROM HOMEWORKS "
                + "WHERE    course_id = ? "
                + "AND      id = ? ";
            pst = c.prepareStatement(sql);
            pst.setString(1, courseID);
            pst.setString(2, homeworkID);
            pst.executeUpdate();
            pst.close();

            /* update to MessageQueue */
            String courseName = getCourseName(courseID);
            pst = c.prepareStatement("INSERT INTO UPDATES VALUES (?, ?, ?, ?, ?, ?)");
            pst.setString(1, getCurTime());
            pst.setString(2, courseName);
            pst.setString(3, homeworkName);
            pst.setString(4, "delete");
            pst.setString(5, "");
            pst.setString(6, "");
            pst.executeUpdate();
            pst.close();

            // TX_END
            st = c.createStatement();
            st.executeUpdate("COMMIT");
            st.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
    }

    /* @brief   查询满足指定条件的课程
     * @param   一个Map, key是查询域, value是查询内容
     *          可选查询域为教师名"teacher", 或课程名"name"
     *          查询内容为结果对应域的子串。信息缺省则传入空串
     * @return  一个List, 包含所有符合要求的课程ID
     *          空List也可能代表查询出错
     */
    public List<String> queryCourse(Map<String, String> query) {
        try {
            ArrayList<String> ret = new ArrayList<>();

            pst = c.prepareStatement("SELECT id     FROM COURSES "
                    + "WHERE name    LIKE ? "
                    + "AND   teacher LIKE ? ");

            pst.setString(1, "%" + query.get("name") + "%");
            pst.setString(2, "%" + query.get("teacher") + "%");
            rs = pst.executeQuery();
            while(rs.next())
                ret.add(rs.getString("id"));

            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public List<String> queryCourse(String name, String teacher) {
        return queryCourse(queryCourseMap(name, teacher));
    }

    /* @brief   获取当前数据库中某个课程的所有作业信息
     * @param   课程ID
     * @return  返回一个List, a包含所有作业的ID
     *          空List也可能代表查询出错
     */
    public List<String> getCourseHomework(String courseID) {
        /*
         * 每次更新一个课程的作业信息时，先获取已有的作业信息
         * 对于已经不存在的作业，调用deleteHomework
         * 否则调用updateHomework
         */
        try {
            ArrayList<String> ret = new ArrayList<>();
            pst = c.prepareStatement("SELECT id     FROM HOMEWORKS "
                    + "WHERE course_id = ? ");
            pst.setString(1, courseID);
            rs = pst.executeQuery();
            while(rs.next())
                ret.add(rs.getString("id"));

            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   获取某个课程中ddl在某个时间范围内的作业
     * @param   ddl的时间区间。时间点的格式为"YYYY-MM-DD HH:MM"
     *          当然，还需要courseID
     * @return  返回一个List, 包含所有作业的ID
     *          空List也可能代表查询出错
     */
    public List<String> queryDDLHomework(String courseID, String startTime, String endTime) {
        try {
            ArrayList<String> ret = new ArrayList<>();
            pst = c.prepareStatement("SELECT id     FROM HOMEWORKS "
                    + "WHERE course_id = ? "
                    + "AND   time_ddl BETWEEN ? AND ? ");
            pst.setString(1, courseID);
            pst.setString(2, startTime);
            pst.setString(3, endTime);
            rs = pst.executeQuery();
            while(rs.next())
                ret.add(rs.getString("id"));

            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   获取某个时刻开始的, 某个课程集合内的作业更新。
     * @param   时间区间, 以及课程集合。
     *          List内部是所有需要查询的课程的id。
     * @return  返回一个List, 包含所有查询结果。
     *          每个查询结果是一个map: key, value即更新信息。
     *          键值包含:
     *            修改时间 "time"
     *            课程名称 "course_name"
     *            作业名称 "homework_name"
     *            修改类型 "type"
     *            旧信息   "old"
     *            新信息   "new"
     *          其中"type"为"new"则说明新发布一条作业
     *                    为"delete"则说明删除一条作业
     *          其余修改项含义见作业信息的键值
     */    

    /*
    public List<Map<String, String>> getUpdate(String time, List<String> courses) {
        List<Map<String, String>> ret = new ArrayList<>();
        // 还没做。按需调用这个函数即可
        return ret;
    }
    */

    // 下面是有关评论功能的
    // public void newPost(String title, String content, String courseID);
    // public void newReply(String content, int postID);
    // public List<Map<String, String>> getRangePost(int first, int last);
    // public List<Map<String, String>> getRangeReply(int first, int last, int postID);
    // public List<Map<String, String>> queryPost(Map<String, String> query);

    public void close() {
        try {
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
    }

    /* @param dbPath:数据库文件的路径
     */
    public DataBase(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite" + dbPath);
            initCourseTable(); // ...
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
