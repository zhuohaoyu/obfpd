package main.java.ui;

import com.alibaba.fastjson.JSONObject;
import main.java.App;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ForumPanel extends JPanel {
    JLabel tabPlacementLabel = null;
    JPanel panelTitle = null;
    JPanel panelContent = null;

    private class ReadMore implements ActionListener {
        JFrame Reply = null;
        String postID = null;
        String title = null;
        List< Map<String, String> > reply;
        public ReadMore(String __postID, List< Map<String, String> > __reply, String __title) {
            postID = __postID;
            reply = __reply;
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

        private void initialize() {
            Reply.setLayout( new MigLayout(
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
                Reply.add(panelTitle, "cell 0 0,alignx left,growx 0");
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
                for (Map<String, String>detail : reply) {
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
                Reply.add(panelPoster, "cell 0 1");
            }

            /******** Reply ********/
            {
                int total = reply.size() - 1;
                String buf = "[]";
                for (int i = 1; i < total; ++i)
                    buf = buf + "5[]";
                JPanel panelReply = new JPanel();
                panelReply.setLayout( new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]",
                        buf
                ));
                for (int i = 0; i <= total; ++i) {
                    Map<String, String> detail = reply.get(i);
                    if (i == 0) continue;

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

                    panelReply.add(panelPoster, "cell 0 " + Integer.toString(i - 1));
                }
                JScrollPane jsp = new JScrollPane(panelReply);
                jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                jsp.setBorder(null);
                jsp.getVerticalScrollBar().setUnitIncrement(16);
                jsp.setPreferredSize(new Dimension(200, 350));
                Reply.add(jsp, "cell 0 2");
            }

            /******** Send ********/
            {
                JPanel panelSend = new JPanel();
                panelSend.setLayout( new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]para",
                        "[grow,fill]para[]"
                ));
                JTextArea jta = new JTextArea();
                jta.setLineWrap(true);
                jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
                JScrollPane jsp = new JScrollPane( jta );
                panelSend.add(jsp, "cell 0 0");
                panelSend.setPreferredSize(new Dimension(200, 90));

                /******** Button ********/
                {
                    JPanel panelButton = new JPanel();
                    panelButton.setLayout( new MigLayout(
                            "insets 0,hidemode 3",
                            "[]10[]",
                            "[grow,fill]para"
                    ));
                    JButton jbok = new JButton("发 送");
                    JButton jbcancle = new JButton("取 消");
                    panelButton.add(jbok, "cell 0 0");
                    panelButton.add(jbcancle, "cell 1 0");
                    panelSend.add(panelButton, "cell 0 1,align right,growx 0");
                }

                Reply.add(panelSend, "cell 0 3");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Reply = new JFrame(title);
            Reply.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Reply.setVisible(true);
            Reply.setBounds( UiConsts.MAIN_WINDOW_X,
                    UiConsts.MAIN_WINDOW_Y,
                    UiConsts.MAIN_WINDOW_WIDTH,
                    UiConsts.MAIN_WINDOW_HEIGHT + 200 ) ;
            initialize();
        }
    }

    private class NewPost implements ActionListener {
        private JFrame NewPost = null;
        private JTextField jtftitile = null;
        private JTextField jtfcourse = null;
        private JTextArea jtacontent = null;

        void initialize() {
            NewPost.setLayout( new MigLayout(
                    "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ",hidemode 3",
                    "[grow,fill]para",
                    "[]10[grow,fill]"
            ));

            /******** Title ********/
            {
                JPanel panelTitle = new JPanel();
                JLabel label = new JLabel("分享新鲜事");
                label.setFont(UiConsts.FONT_TITLE0);
                panelTitle.add(label);
                NewPost.add(panelTitle, "cell 0 0,alignx left,growx 0");
            }

            /******** Post Content ********/
            {
                JPanel panelContent = new JPanel();
                panelContent.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[grow,fill]para",
                        "[]10[]10[grow,fill]10[]"
                ));
                {
                    JPanel panelCourse = new JPanel();
                    panelCourse.setLayout(new MigLayout(
                            "insets 0,hidemode 3",
                            "[]10[grow,fill]para",
                            "[grow,fill]"
                    ));
                    JLabel label = new JLabel("课 程：");
                    label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 4f));
                    jtfcourse = new JTextField();
                    jtfcourse.setFont(jtfcourse.getFont().deriveFont(jtfcourse.getFont().getSize() + 4f));
                    panelCourse.add(label, "cell 0 0");
                    panelCourse.add(jtfcourse, "cell 1 0");
                    panelContent.add(panelCourse, "cell 0 0");
                }
                {
                    JPanel panelCourse = new JPanel();
                    panelCourse.setLayout(new MigLayout(
                            "insets 0,hidemode 3",
                            "[]10[grow,fill]para",
                            "[grow,fill]"
                    ));
                    JLabel label = new JLabel("标 题：");
                    label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 4f));
                    jtftitile = new JTextField();
                    jtftitile.setFont(jtftitile.getFont().deriveFont(jtftitile.getFont().getSize() + 4f));
                    panelCourse.add(label, "cell 0 0");
                    panelCourse.add(jtftitile, "cell 1 0");
                    panelContent.add(panelCourse, "cell 0 1");
                }
                {
                    JPanel panelCourse = new JPanel();
                    panelCourse.setLayout(new MigLayout(
                            "insets 0,hidemode 3",
                            "[]10[grow,fill]para",
                            "[grow,fill]"
                    ));
                    JLabel label = new JLabel("内 容：");
                    label.setFont(label.getFont().deriveFont(label.getFont().getSize() + 4f));
                    jtacontent = new JTextArea();
                    jtacontent.setFont(jtacontent.getFont().deriveFont(jtacontent.getFont().getSize() + 4f));
                    JScrollPane jsp = new JScrollPane(jtacontent);
                    panelCourse.add(label, "cell 0 0");
                    panelCourse.add(jsp, "cell 1 0");
                    panelContent.add(panelCourse, "cell 0 2");
                }
                {
                    JPanel panelButton = new JPanel();
                    panelButton.setLayout(new MigLayout(
                            "insets 0,hidemode 3",
                            "[]20[]20",
                            "[grow,fill]"
                    ));
                    JButton jbok = new JButton("发 送");
                    JButton jbcancle = new JButton("取 消");
                    jbok.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Task", "createPOST");
                            jsonObject.put("Title", jtftitile.getText());
                            jsonObject.put("Course", jtfcourse.getText());
                            jsonObject.put("Content", jtacontent.getText());
                            jsonObject.put("userID", App.username);
                            App.myclient.send(jsonObject);
                            NewPost.dispose();
                        }
                    });
                    jbcancle.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            NewPost.dispose();
                        }
                    });
                    panelButton.add(jbok, "cell 0 0");
                    panelButton.add(jbcancle, "cell 1 0");
                    panelContent.add(panelButton, "cell 0 3,align right,growx 0");
                }
                NewPost.add(panelContent, "cell 0 1");
/*
                {
                    JPanel panelPassword = new JPanel();
                    panelPassword.setLayout(new MigLayout(
                            "insets 0,hidemode 3",
                            "[][grow,fill]para",
                            "[grow,fill]"
                    ));
                    JLabel label = new JLabel("密码：");
                    jtapw = new JPasswordField ();
                    panelPassword.add(label, "cell 0 0");
                    panelPassword.add(jtapw, "cell 1 0");
                    add(panelPassword, "cell 0 2");
                }

                {
                    JPanel panelButton = new JPanel();
                    panelButton.setLayout(new MigLayout(
                            "insets 0,hidemode 3",
                            "[]10[]",
                            "[grow,fill]"
                    ));
                    JButton jbok = new JButton("确 定");
                    JButton jbcancle = new JButton("取 消");
                }
*/
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NewPost = new JFrame("分享新鲜事");
            NewPost.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            NewPost.setVisible(true);
            NewPost.setBounds( UiConsts.MAIN_WINDOW_X,
                    UiConsts.MAIN_WINDOW_Y,
                    UiConsts.MAIN_WINDOW_WIDTH,
                    UiConsts.MAIN_WINDOW_HEIGHT ) ;
            initialize();
        }
    }

    public ForumPanel(){
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
        jta.setText("Title: \n" + title + ".\n\nUser:  " + user);
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

        List< Map<String, String> > content = new ArrayList<>();
        Map<String, String> mp = new HashMap<String, String>();
        for (int i = 1; i <= 10; ++i) {
            mp = new HashMap<String, String>();
            mp.put("content", "orz" + Integer.toString(i));
            mp.put("time", "2021-6-5 23:27:17");
            mp.put("posterID", Integer.toString(i));
            content.add(mp);
        }
        ReadMoreButton.addActionListener(new ReadMore(postID, content, title));

        return panel;
    }

    private void initialize(){
        tabPlacementLabel = new JLabel();

        setName("this");
        setLayout( new MigLayout(
                "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ", hidemode 3",
                "[grow,fill]para",
                "[][]"
        ));

        panelTitle = new JPanel();
        panelTitle.setName("panel1");
        panelTitle.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                "[grow, fill]para",
                "[grow, fill]para"));

        JPanel panelTitle = new JPanel();
        panelTitle.setLayout( new MigLayout(
                "insets 0, hidemode 3",
                "[][grow,fill]para[]",
                "[]"
        ));
        tabPlacementLabel.setText("论坛");
        tabPlacementLabel.setFont(UiConsts.FONT_TITLE0);
        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( UiConsts.INF_WIDTH , 20 ) ) ;
        panelTitle.add(tabPlacementLabel, "cell 0 0");

        JButton jbnew = new JButton();
        jbnew.setText("分享新鲜事");
        jbnew.addActionListener(new NewPost());
        panelTitle.add(jbnew, "cell 2 0");

        add(panelTitle, "cell 0 0");

        panelContent = new JPanel();
        panelContent.setName("panel");
        panelContent.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                // columns
                "[grow,fill]para",
                // rows
                "[]15[]15[]15[]15[]"));

        for (int i = 1; i <= 5; ++i) {
            panelContent.add(createPOST("flkjaskldjfikljashdkjlfhasjkldfhjklashdfjklahsdjklfhjklashdfjklhasdjklfhjklasehdfjklhskl",
                    "lkfasjsdkljfaklsdjflasdkjfklasjdfljasdlfjkaslkdjflkasd",
                    "test"),
                    "cell 0 " + Integer.toString(i));
        }
        JScrollPane jsp = new JScrollPane(panelContent);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setBorder(null);
        jsp.getVerticalScrollBar().setUnitIncrement(16);
        add(jsp, "cell 0 1");
    }
}
