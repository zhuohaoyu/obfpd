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
        var pst2 = c.prepareStatement("SELECT name FROM COURSES WHERE id = ?");
        pst2.setString(1, courseID);
        var rs2 = pst2.executeQuery();
        rs2.next();
        String courseName = rs2.getString("name");
        rs2.close();
        pst2.close();
        return courseName;
    }

    /* @brief   内部函数 通过作业ID获取作业名
     */
    public String getHomeworkName(String homeworkID, String courseID) throws SQLException {
        var pst2 = c.prepareStatement("SELECT name FROM HOMEWORKS WHERE course_id = ? AND id = ?");
        pst2.setString(1, courseID);
        pst2.setString(2, homeworkID);
        var rs2 = pst2.executeQuery();
        rs2.next();
        String homeworkName = rs2.getString("name");
        rs2.close();
        pst2.close();
        return homeworkName;
    }

    /* @brief  获取 "yyyy-MM-dd HH:mm:ss" 格式的当前时间 | 现在不确定时区有没有问题
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

    /* @brief   生成一个作业更新对应的Map
     */
    public Map<String, String> updateMap(String time, String courseName,
                                         String homeworkName, String type,
                                         String oldContent, String newContent) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("time", time);
        retMap.put("course_name", courseName);
        retMap.put("homework_name", homeworkName);
        retMap.put("type", type);
        retMap.put("old", oldContent);
        retMap.put("new", newContent);
        return retMap;
    }

    /* @brief   生成一个讨论帖对应的Map
     * @param   见getRangePost方法
     */
    public Map<String, String> postMap(String id, String posterID, String time,
                                       String title, String topic, String timeReply) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("id", id);
        retMap.put("poster_id", posterID);
        retMap.put("time", time);
        retMap.put("title", title);
        retMap.put("topic", topic);
        retMap.put("time_reply", timeReply);
        return retMap;
    }

    /* @brief   生成一条回复对应的Map
     * @param   见getPostReply方法
     */
    public Map<String, String> replyMap(String id, String posterID, String time,
                                        String content, String postID) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("id", id);
        retMap.put("poster_id", posterID);
        retMap.put("time", time);
        retMap.put("content", content);
        retMap.put("post_id", postID);
        return retMap;
    }

    /* @brief   生成一个讨论帖查询对应的Map
     * @param   见queryPost方法
     */
    public Map<String, String> queryPostMap(String title, String topic,
                                            String posterID) {
        Map<String, String> retMap = new HashMap<>();
        retMap.put("title", title);
        retMap.put("topic", topic);
        retMap.put("poster_id", posterID);
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
                    + "    course_id       CHAR(16)    NOT NULL,"
                    + "    homework_id     INTEGER     NOT NULL,"
                    + "    type            TEXT        NOT NULL,"
                    + "    old             TEXT,"
                    + "    new             TEXT"
                    + ")";
            st.executeUpdate(sql);
            sql = "CREATE INDEX time_index on UPDATES(time)";
            st.executeUpdate(sql);
            System.out.println("Created table \"UPDATES\" successfully");

            sql = "CREATE TABLE POSTS ("
                    + "    id              INTEGER     PRIMARY KEY  AUTOINCREMENT,"
                    + "    poster_id       INTEGER     NOT NULL,"
                    + "    time            TEXT        NOT NULL,"
                    + "    title           TEXT        NOT NULL,"
                    + "    topic           TEXT,"
                    + "    time_reply      TEXT        NOT NULL"
                    + ")";
            st.executeUpdate(sql);
            System.out.println("Created table \"POSTS\" successfully");

            sql = "CREATE TABLE REPLIES ("
                    + "    id              INTEGER     PRIMARY KEY  AUTOINCREMENT,"
                    + "    poster_id       INTEGER     NOT NULL,"
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
            if(rs.next()) throw new SQLSyntaxErrorException("Course already exists: " + course.get("name"));
            pst.close();

            pst = c.prepareStatement("INSERT INTO COURSES VALUES (?, ?, ?)");
            pst.setString(1, course.get("id"));
            pst.setString(2, course.get("name"));
            pst.setString(3, course.get("teacher"));
            pst.executeUpdate();
            pst.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
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
                    pst.setInt(7, Integer.parseInt(homework.get("id")));
                    pst.executeUpdate();
                    pst.close();

                    /* update to MessageQueue */
                    // String courseName = getCourseName(homework.get("course_id"));

                    for(int i = 0; i < colList.size(); i += 1) {
                        pst = c.prepareStatement("INSERT INTO UPDATES VALUES (?, ?, ?, ?, ?, ?)");
                        pst.setString(1, getCurTime());
                        pst.setString(2, homework.get("course_id"));
                        pst.setInt(3, Integer.parseInt(homework.get("id")));
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
                pst.setInt(1, Integer.parseInt(homework.get("id")));
                pst.setString(2, homework.get("name"));
                pst.setString(3, homework.get("time_pull"));
                pst.setString(4, homework.get("time_ddl"));
                pst.setString(5, homework.get("description"));
                pst.setString(6, homework.get("attachment"));
                pst.setString(7, homework.get("course_id"));
                pst.executeUpdate();
                pst.close();

                pst = c.prepareStatement("INSERT INTO UPDATES VALUES (?, ?, ?, ?, ?, ?)");
                pst.setString(1, getCurTime());
                pst.setString(2, homework.get("course_id"));
                pst.setInt(3, Integer.parseInt(homework.get("id")));
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    /* @brief   查询满足指定条件的课程
     * @param   一个Map, key是查询域, value是查询内容
     *          可选检索内容为:
     *            "teacher"     教师名
     *            "name"        课程名
     *          查询内容为结果对应域的子串。
     *          信息缺省则传入空串。
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   获取某个时刻开始到现在的, 某个课程集合内的作业更新。
     * @param   时间节点, 以及课程集合。
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
    public List<Map<String, String>> getUpdate(String time, List<String> courses) {
        try {
            List<Map<String, String>> ret = new ArrayList<>();
            StringBuilder sb = new StringBuilder("SELECT * FROM UPDATES WHERE time BETWEEN ? AND ? ");
            sb.append("AND course_id in ( ");
            for(int i = 0; i < courses.size(); i += 1) {
                if(i != 0) sb.append(", ");
                sb.append("\'");
                sb.append(courses.get(i));
                sb.append("\'");
            }
            sb.append(")");

            System.err.println(sb.toString());

            pst = c.prepareStatement(sb.toString());
            pst.setString(1, time);
            pst.setString(2, getCurTime());
            rs = pst.executeQuery();
            while(rs.next()) {
                ret.add(updateMap(rs.getString("time"),
                        getCourseName(rs.getString("course_id")),
                        getHomeworkName(rs.getString("homework_id"), rs.getString("course_id")),
                        rs.getString("type"),
                        rs.getString("old"),
                        rs.getString("new"))
                );
            }
            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   删除数据库中某个时刻以前的作业更新。
     * @param   时间节点
     */
    public void clearUpdate(String time) {
        try {
            pst = c.prepareStatement("DELETE FROM UPDATES WHERE time BETWEEN ? AND ? ");
            pst.setString(1, "2000-01-01 00:00:00"); // 不确定时间格式是否对齐
            pst.setString(2, time);
            pst.executeUpdate();
            pst.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 下面是有关评论功能的

    /* @brief   添加一个讨论帖。
     * @param   标题，内容，話題，发布者编号
     * @return  是否添加成功
     */
    public boolean newPost(String title, String content, String topic, Integer posterID) {
        System.err.println("Now create post #-1 : title = " + title);
        try {
            // TX_BEGIN
            st = c.createStatement();
            st.executeUpdate("BEGIN");
            st.close();

            // System.err.println("Now create post #0 : courseName = " + courseName);

            /*
            pst = c.prepareStatement("SELECT * FROM COURSES WHERE id = ? ");
            pst.setString(1, courseName);
            rs = pst.executeQuery();
            if(!rs.next()) throw new SQLSyntaxErrorException("Add new post failed: Invalid course name.");
            String courseID = rs.getString("id");
            rs.close();
            pst.close();
            */

            System.err.println("Now create post #1 : title = " + title);

            pst = c.prepareStatement("INSERT INTO POSTS "
                    + "(poster_id, time, title, topic, time_reply) "
                    + "VALUES (?, ?, ?, ?, ?)");
            pst.setInt(1, posterID);
            pst.setString(2, getCurTime());
            pst.setString(3, title);
            pst.setString(4, topic);
            pst.setString(5, getCurTime());
            pst.executeUpdate();
            pst.close();

            // System.err.println("Now create post #2: title = " + title);

            st = c.createStatement();
            rs = st.executeQuery("select last_insert_rowid() from POSTS");
            rs.next();
            Integer postID = rs.getInt(1);
            rs.close();
            st.close();
            if(postID == 0) throw new SQLSyntaxErrorException("Add new post failed.");

            // System.err.println("Now create post #3: title = " + title);

            pst = c.prepareStatement("INSERT INTO REPLIES (poster_id, time, content, post_id)"
                    + "VALUES (?, ?, ?, ?)");
            pst.setInt(1, posterID);
            pst.setString(2, getCurTime());
            pst.setString(3, content);
            pst.setInt(4, postID);
            pst.executeUpdate();
            pst.close();

            // System.err.println("Now create post #4: title = " + title);

            // TX_END
            st = c.createStatement();
            st.executeUpdate("COMMIT");
            st.close();

            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /* @brief   添加一条回复。
     * @param   内容，讨论帖编号
     * @return  是否添加成功
     */
    public boolean newReply(String content, int postID, int posterID) {
        try {
            // TX_BEGIN
            st = c.createStatement();
            st.executeUpdate("BEGIN");
            st.close();

            pst = c.prepareStatement("INSERT INTO REPLIES (poster_id, time, content, post_id)"
                    + "VALUES (?, ?, ?, ?)");
            pst.setInt(1, posterID);
            pst.setString(2, getCurTime());
            pst.setString(3, content);
            pst.setInt(4, postID);
            pst.executeUpdate();
            pst.close();

            pst = c.prepareStatement("UPDATE POSTS SET time_reply = ? WHERE id = ? ");
            pst.setString(1, getCurTime());
            pst.setInt(2, postID);
            pst.executeUpdate();
            pst.close();

            // TX_END
            st = c.createStatement();
            st.executeUpdate("COMMIT");
            st.close();

            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /* @brief   获取第 [first, last] 新的讨论帖(1-indexed)。
     * @return  返回一个List, 包含所有查询结果。
     *          如果last超过课程总数，不会抛出异常。
     *          每个查询结果是一个map: key, value即更新信息。
     *          键值包含:
     *            讨论帖编号    "id"
     *            发布者编号    "poster_id"
     *            发布时间      "time"
     *            标题          "title"
     *            話題名稱      "topic"
     *            最近回复时间  "time_reply"
     */
    public List<Map<String, String>> getRangePost(int first, int last) {
        try {
            List<Map<String, String>> ret = new ArrayList<>();

            st = c.createStatement();
            rs = st.executeQuery("select count(*) from POSTS");
            rs.next();
            Integer totalPost = rs.getInt(1);
            rs.close();
            st.close();

            pst = c.prepareStatement("SELECT * FROM POSTS WHERE id BETWEEN ? AND ?");
            pst.setInt(1, totalPost + 1 - last);
            pst.setInt(2, totalPost + 1 - first);
            rs = pst.executeQuery();
            while(rs.next()) {
                ret.add(postMap(rs.getString("id"),
                        rs.getString("poster_id"),
                        rs.getString("time"),
                        rs.getString("title"),
                        rs.getString("topic"),
                        rs.getString("time_reply")
                ));
                System.out.println("id="+(rs.getString("id")) + "/" + totalPost.toString());
            }
            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   获取某个讨论帖的所有回复。
     * @return  返回一个List, 包含所有查询结果。
     *          每个查询结果是一个map: key, value即更新信息。
     *          键值包含:
     *            回复编号    "id"
     *            发布者编号  "poster_id"
     *            发布时间    "time"
     *            内容        "content"
     *            讨论帖编号  "post_id"
     */
    public List<Map<String, String>> getPostReply(int postID) {
        try {
            List<Map<String, String>> ret = new ArrayList<>();

            pst = c.prepareStatement("SELECT * FROM REPLIES WHERE post_id = ?");
            pst.setInt(1, postID);
            rs = pst.executeQuery();
            while(rs.next()) {
                ret.add(replyMap(rs.getString("id"),
                        rs.getString("poster_id"),
                        rs.getString("time"),
                        rs.getString("content"),
                        rs.getString("post_id"))
                );
            }
            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   查询满足指定条件的讨论帖。
     * @param   一个Map, key是查询域, value是查询内容
     *          可选检索内容为:
     *            标题          "title"
     *            話題         "topic"
     *            发布者编号    "poster_id"
     *          除发布者编号外，查询内容为结果对应域的子串。
     *          信息缺省则传入空串。
     * @return  一个List, 包含所有符合要求的课程ID
     *          空List也可能代表查询出错
     */
    public List<Map<String, String>> queryPost(Map<String, String> query) {
        try {
            ArrayList<Map<String, String>> ret = new ArrayList<>();
            String sql = "SELECT    *      FROM POSTS "
                    + "WHERE     poster_id BETWEEN ? AND ? "
                    + "AND       title LIKE ? "
                    + "AND       topic = ?";

            pst = c.prepareStatement(sql);

            if(query.containsKey("poster_id")) {
                pst.setInt(1, Integer.parseInt(query.get("poster_id")));
                pst.setInt(2, Integer.parseInt(query.get("poster_id")));
            } else {
                pst.setInt(1, 0);
                pst.setInt(2, 2147483647);
            }
            pst.setString(3, "%" + query.get("title") + "%");
            pst.setString(4, query.get("topic"));
            rs = pst.executeQuery();
            while(rs.next()) {
                ret.add(postMap(rs.getString("id"),
                        rs.getString("poster_id"),
                        rs.getString("time"),
                        rs.getString("title"),
                        rs.getString("topic"),
                        rs.getString("time_reply")
                ));
            }

            rs.close();
            pst.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /* @brief   关闭查询
     */
    public void close() {
        try {
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* @param   数据库文件的路径
     */
    public DataBase(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            initCourseTable(); // ...
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
