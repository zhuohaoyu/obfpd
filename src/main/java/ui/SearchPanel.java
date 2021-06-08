package main.java.ui;

import com.alibaba.fastjson.JSONObject;
import main.java.App;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.server.ExportException;
import java.util.*;
import java.util.List;

public class SearchPanel extends JPanel {
    JLabel tabPlacementLabel = null;
    JPanel panelTitle = null;
    JPanel panelContent = null;
    JScrollPane jsPanel = null;
    JTextField jtftitile = null;

    public class ReadMore implements ActionListener {
        public JFrame Reply = null;
        public JPanel jpr = null;
        public JTextArea jtacontent =null;
        public JPanel panelReply = null;
        public JScrollPane jspReply = null;
        String postID = null;
        String title = null;
        public ReadMore(String __postID, String __title) {
            postID = __postID;
            title = __title;
        }

        private JPanel createREPLY(Map <String, String> detail, boolean flag) {
            JPanel panel = new JPanel();
            panel.setLayout(new MigLayout(
                    "insets 0,hidemode 3",
                    "[grow,fill]para",
                    "[grow,fill]"
            ));
            JTextArea jta = new JTextArea();
            jta.setText(detail.get("content"));
            jta.setLineWrap(true);
            jta.setEnabled(false);
            jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
            JScrollPane jsp = new JScrollPane( jta );
            panel.add(jsp, "cell 0 0");
            if (flag)
                panel.setPreferredSize(new Dimension(200, 90));
            else
                panel.setPreferredSize(new Dimension(200, 60));
            return panel;
        }

        private void refresh() {
            if (jspReply != null) {
                jpr.remove(jspReply);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Task", "queryREPLY");
            jsonObject.put("postID", postID);
            ForumPanel.isQueryREPLYFinished = false;
            ForumPanel.reply.clear();
            App.myclient.send(jsonObject);

            while (!ForumPanel.isQueryREPLYFinished) {
                try{
                    Thread.sleep(100);
                }
                catch (Exception e) {  }
            }
            ForumPanel.isQueryREPLYFinished = false;
            int sum = 0;
            int total = ForumPanel.reply.size() - 1;
            String buf = "[]";
            for (int i = 1; i < total; ++i)
                buf = buf + "5[]";
            panelReply = new JPanel();
            panelReply.setLayout( new MigLayout(
                    "insets 0,hidemode 3",
                    "[grow,fill]",
                    buf
            ));
            System.err.println("in refresh: " + total);
            for (int i = 0; i <= total; ++i) {
                Map<String, String> detail = ForumPanel.reply.get(i);
                System.err.println("refresh: " + detail.get("postID") + "  " + postID);
                if (!detail.get("postID").equals(postID)) continue;
                if (sum == 0) {
                    sum += 1;
                    continue;
                }

                JLabel user = new JLabel();
                JLabel ptti = new JLabel();
                JPanel panelPoster = new JPanel();
                panelPoster.setLayout( new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]",
                        "[grow,fill][]"
                ));
                panelPoster.add(createREPLY(detail, true), "cell 0 0");

                {
                    JPanel panel = new JPanel();
                    panel.setLayout( new MigLayout(
                            "insets 0,hidemode 3",
                            "[]20[]20",
                            "[]"
                    ));
                    user.setText(detail.get("posterID"));
                    ptti.setText(detail.get("time"));
                    panel.add(user, "cell 0 0");
                    panel.add(ptti, "cell 1 0");
                    panelPoster.add(panel, "cell 0 1,align right,growx 0");
                }

                panelReply.add(panelPoster, "cell 0 " + Integer.toString(sum - 1));
                sum += 1;
            }
            jspReply = new JScrollPane(panelReply);
            jspReply.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            jspReply.setBorder(null);
            jspReply.getVerticalScrollBar().setUnitIncrement(16);
            jspReply.setPreferredSize(new Dimension(200, 350));
            jpr.add(jspReply, "cell 0 2");

            jpr.add(jspReply, "cell 0 2");
            SwingUtilities.invokeLater(() -> this.jpr.updateUI());
        }

        private void initialize() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Task", "queryREPLY");
            jsonObject.put("postID", postID);
            ForumPanel.isQueryREPLYFinished = false;
            ForumPanel.reply.clear();
            App.myclient.send(jsonObject);

            while (!ForumPanel.isQueryREPLYFinished) {
                try{
                    Thread.sleep(100);
                }
                catch (Exception e) {  }
            }
            ForumPanel.isQueryREPLYFinished = false;

            System.err.println(ForumPanel.reply);
            jpr.setLayout( new MigLayout(
                    "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ",hidemode 3",
                    "[grow,fill]para",
                    "[]10[]10[]10[grow,fill]"
            ));

            /******** Title ********/
            {
                JPanel panelTitle = new JPanel();
                JLabel label = new JLabel("帖子详情");
                label.setFont(UiConsts.FONT_TITLE0);
                panelTitle.add(label);
                jpr.add(panelTitle, "cell 0 0,alignx left,growx 0");
            }

            /******** Poster ********/
            {
                JPanel panelPoster = new JPanel();
                panelPoster.setLayout( new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]",
                        "[grow,fill][]"
                ));
                JLabel user = new JLabel();
                JLabel ptti = new JLabel();
                for (int i = 0; i < ForumPanel.reply.size(); ++i) {
                    Map<String, String> detail = ForumPanel.reply.get(i);
                    if (!detail.get("postID").equals(postID)) continue;
                    panelPoster.add(createREPLY(detail, true), "cell 0 0");

                    {
                        JPanel panel = new JPanel();
                        panel.setLayout( new MigLayout(
                                "insets 0,hidemode 3",
                                "[]20[]20",
                                "[grow,fill]para"
                        ));
                        user.setText(detail.get("posterID"));
                        ptti.setText(detail.get("time"));
                        panel.add(user, "cell 0 0");
                        panel.add(ptti, "cell 1 0");
                        panelPoster.add(panel, "cell 0 1,align right,growx 0");
                    }

                    break;
                }
                panelPoster.setPreferredSize(new Dimension(200, 120));
                jpr.add(panelPoster, "cell 0 1");
            }

            /******** Reply ********/
            {
                int sum = 0;
                int total = ForumPanel.reply.size() - 1;
                String buf = "[]";
                for (int i = 1; i < total; ++i)
                    buf = buf + "5[]";
                panelReply = new JPanel();
                panelReply.setLayout( new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]",
                        buf
                ));
                for (int i = 0; i <= total; ++i) {
                    Map<String, String> detail = ForumPanel.reply.get(i);
                    if (!detail.get("postID").equals(postID)) continue;
                    if (sum == 0) {
                        sum += 1;
                        continue;
                    }

                    JLabel user = new JLabel();
                    JLabel ptti = new JLabel();
                    JPanel panelPoster = new JPanel();
                    panelPoster.setLayout( new MigLayout(
                            "insets 0,hidemode 3",
                            "[grow,fill]",
                            "[grow,fill][]"
                    ));
                    panelPoster.add(createREPLY(detail, true), "cell 0 0");

                    {
                        JPanel panel = new JPanel();
                        panel.setLayout( new MigLayout(
                                "insets 0,hidemode 3",
                                "[]20[]20",
                                "[]"
                        ));
                        user.setText(detail.get("posterID"));
                        ptti.setText(detail.get("time"));
                        panel.add(user, "cell 0 0");
                        panel.add(ptti, "cell 1 0");
                        panelPoster.add(panel, "cell 0 1,align right,growx 0");
                    }

                    panelReply.add(panelPoster, "cell 0 " + Integer.toString(sum - 1));
                    sum += 1;
                }
                jspReply = new JScrollPane(panelReply);
                jspReply.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jspReply.setBorder(null);
                jspReply.getVerticalScrollBar().setUnitIncrement(16);
                jspReply.setPreferredSize(new Dimension(200, 350));
                jpr.add(jspReply, "cell 0 2");
            }

            /******** Send ********/
            {
                JPanel panelSend = new JPanel();
                panelSend.setLayout( new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]para",
                        "[grow,fill]para[]"
                ));
                jtacontent = new JTextArea();
                jtacontent.setLineWrap(true);
                jtacontent.setFont(jtacontent.getFont().deriveFont(jtacontent.getFont().getSize() + 4f));
                JScrollPane jsp = new JScrollPane( jtacontent );
                panelSend.add(jsp, "cell 0 0");
                panelSend.setPreferredSize(new Dimension(200, 90));

                /******** Button ********/
                {
                    JPanel panelButton = new JPanel();
                    panelButton.setLayout( new MigLayout(
                            "insets 0,hidemode 3",
                            "[][grow,fill][]10[]",
                            "[grow,fill]para"
                    ));
                    JButton jbrf = new JButton("刷 新");
                    JButton jbok = new JButton("发 送");
                    JButton jbcancle = new JButton("取 消");

                    jbrf.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            refresh();
                        }
                    });
                    jbok.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Task", "createREPLY");
                            jsonObject.put("content", jtacontent.getText());
                            jsonObject.put("postID", postID);
                            jsonObject.put("userID", App.username);
                            ForumPanel.isQueryREPLYFinished = false;
                            App.myclient.send(jsonObject);
                            refresh();
                        }
                    });
                    jbcancle.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Reply.dispose();
                        }
                    });

                    panelButton.add(jbrf, "cell 0 0,align left,growx 0");
                    panelButton.add(jbok, "cell 2 0");
                    panelButton.add(jbcancle, "cell 3 0");
                    panelSend.add(panelButton, "cell 0 1,align right,growx 0");
                }

                jpr.add(panelSend, "cell 0 3");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Reply = new JFrame(title);
            jpr = new JPanel();
            Reply.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Reply.setVisible(true);
            Reply.setBounds( UiConsts.MAIN_WINDOW_X,
                    UiConsts.MAIN_WINDOW_Y,
                    UiConsts.MAIN_WINDOW_WIDTH,
                    UiConsts.MAIN_WINDOW_HEIGHT + 200 ) ;
            Reply.add(jpr, BorderLayout.CENTER);
            initialize();
        }
    }

    public void ReFresh() {
        if (App.isForum == true) {
            initialize();
            App.mainPanelCenter.add(App.forumPanel, BorderLayout.CENTER);
            SwingUtilities.invokeLater(() -> App.mainPanelCenter.updateUI());
        }
    }

    public SearchPanel(){
        setName("this");
        setLayout( new MigLayout(
                "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ", hidemode 3",
                "[grow,fill]para",
                "[][][]"
        ));
        tabPlacementLabel = new JLabel();

        panelTitle = new JPanel();
        panelTitle.setName("panel1");
        panelTitle.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                "[grow, fill]para",
                "[grow, fill]para"));

        JPanel panelTitle = new JPanel();
        panelTitle.setLayout( new MigLayout(
                "insets 0, hidemode 3",
                "[grow,fill]",
                "[][]"
        ));
        tabPlacementLabel.setText("搜索");
        tabPlacementLabel.setFont(UiConsts.FONT_TITLE0);
        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( UiConsts.INF_WIDTH , 20 ) ) ;
        panelTitle.add(tabPlacementLabel, "cell 0 0,align left,growx 0");

        JPanel panelCourse = new JPanel();
        panelCourse.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                "[]10[grow,fill]10[]",
                "[grow,fill]"
        ));
        JLabel label = new JLabel("标 题：");
        label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 4f));
        jtftitile = new JTextField();
        jtftitile.setFont(jtftitile.getFont().deriveFont(jtftitile.getFont().getSize() + 4f));
        JButton jbs = new JButton();
        jbs.setText("搜一搜");
        panelCourse.add(label, "cell 0 0");
        panelCourse.add(jtftitile, "cell 1 0");
        panelCourse.add(jbs, "cell 2 0");
        panelTitle.add(panelCourse, "cell 0 1");

        add(panelTitle, "cell 0 0");
        initialize() ;
    }

    private JPanel createPOST(String title, String user, String postID) {
        JPanel panel = new JPanel();
        panel = new JPanel();
        panel.setName("panel");
        panel.setLayout( new MigLayout(
                "insets 0,hidemode 3",
                "[grow,fill]para",
                "[][]"
        ));
        JTextArea jta = new JTextArea();
        jta.setLineWrap(true);
        jta.setEnabled(false);
        jta.setText("Title: " + title + ".\n\nUser:  " + user);
        jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
        JScrollPane jsp = new JScrollPane( jta );
        panel.add(jsp, "cell 0 0");
        panel.setPreferredSize(new Dimension(200, 120));
        JButton ReadMoreButton = new JButton();
        ReadMoreButton.setText("Read More");
        ReadMoreButton.setSelected(true);
        ReadMoreButton.setFont(ReadMoreButton.getFont().deriveFont(ReadMoreButton.getFont().getSize() + 0f));
        ReadMoreButton.setName("ReadMoreButton");
        panel.add(ReadMoreButton, "cell 0 1");
        ReadMoreButton.addActionListener(new ReadMore(postID, title));

        return panel;
    }

    public void initialize(){
        ForumPanel.post.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Task", "queryPOST");
        App.myclient.send(jsonObject);

        while (!ForumPanel.isQueryPOSTFinished) {
            try {
                Thread.sleep(100);
            }
            catch (Exception e) { }
        }
        ForumPanel.isQueryPOSTFinished = false;

        if (jsPanel != null) {
            remove(panelContent);
            remove(jsPanel);
        }

        String buf = "[]";
        int total = ForumPanel.post.size() - 1;
        System.err.println("in forum panel: " + total);
        for (int i = 1; i <= total; ++i)
            buf = buf + "15[]";
        panelContent = new JPanel();
        panelContent.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                // columns
                "[grow,fill]para",
                // rows
                buf));
        for (int i = total; i >= 0; --i) {
            Map<String, String> detail = ForumPanel.post.get(i);
            System.err.println("search: " + jtftitile.getText());
            System.err.println("thispost: " + detail.get("Title"));
            if (!detail.get("Title").contains(jtftitile.getText())) continue;
            panelContent.add(createPOST(detail.get("Title"),
                    detail.get("userID"),
                    detail.get("postID")),
                    "cell 0 " + Integer.toString(total - i));
        }
        if (total == -1) {
            JLabel label = new JLabel();
            label.setText("暂时没有帖子");
            label.setFont(UiConsts.FONT_TITLE2);
            panelContent.add(label, "cell 0 0");
        }
        jsPanel = new JScrollPane(panelContent);
        jsPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsPanel.setBorder(null);
        jsPanel.getVerticalScrollBar().setUnitIncrement(16);
        add(jsPanel, "cell 0 1");
    }
}
