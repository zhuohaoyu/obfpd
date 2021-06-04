package main.java.server;

import java.sql.*;
import java.util.*;

/* 数据库的更新都具有一定鲁棒性，不需要担心元素重复插入的问题
 * 所有的返回值都只返回ID。你那边爬下来的数据应该是ID唯一映射到其他信息
 * 想到什么新功能随时和我这边说
 */
public class DataBase {
    Connection c = null;
    Statement st = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

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

    /* @brief   在没有表的时候创建新表
     */
    public void initCourseTable() {
        try {
            String sql;
            st = c.createStatement();
            /* sqlite 不支持 if not exists */
            sql = "CREATE TABLE COURSES ("
                    + "    id              CHAR(16)    PRIMARY KEY     NOT NULL,"
                    + "    name            TEXT        NOT NULL,"
                    + "    teacher         TEXT        NOT NULL"
                    + ")";
            st.executeUpdate(sql);
            System.out.println("Created table \"COURSES\" successfully");

            st.executeUpdate("PRAGMA foreign_keys = ON");
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

            if(rs.next()) {
                int cnt = 0;
                for(var entry: homework.entrySet()) {
                    if(!entry.getValue().equals(rs.getString(entry.getKey()))) {
                        /* update to MessageQueue */
                        cnt += 1;
                        System.out.println("Update: " + rs.getString("id") + "/" + rs.getString(entry.getKey()));
                    }
                }
                pst.close();
                if (cnt != 0) {
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
            }
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
        /* 如果实在不希望传courseID和我说一声 */
        try {
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
     * @return  返回一个List, a包含所有作业的ID
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

    public void close() {
        try {
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            // e.printStackTrace();
        }
    }

    /* 等会把指定数据库路径作为参数加上 */
    public DataBase() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            initCourseTable(); // ...
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
