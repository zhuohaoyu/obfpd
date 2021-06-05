package main.java.ui;

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
        List< Map<String, String> > reply;
        public ReadMore(String __postID, List< Map<String, String> > __reply) {
            postID = __postID;
            reply = __reply;
        }

        private JPanel createREPLY(Map <String, String> detail) {
            JPanel jp = new JPanel();
            return jp;
        }

        private void initialize() {
            Reply.setLayout( new MigLayout(
                    "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ",hidemode 3",
                    "[grow,fill]para",
                    "[]10[]10[grow,fill]10[]"
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
//            {
//                for (Map<String, String>detail : reply) {
//                    Reply.add(createREPLY(detail), "cell 0 1");
//                    break;
//                }
//            }

            /******** Reply ********/
//            {
//                int sum = 0;
//                for (Map<String, String>detail : reply) {
//                    sum += 1;
//                    if (sum == 1) continue;
//                    if (sum > 5) break;
//                    Reply.add(createREPLY(detail), "cell 0 1");
//                }
//            }

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
                Reply.add(panelSend, "cell 0 3");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Reply = new JFrame("Forum");
            Reply.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Reply.setVisible(true);
            Reply.setBounds( UiConsts.MAIN_WINDOW_X,
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

        List< Map<String, String> > content = new LinkedList<>();
        Map<String, String> mp = new HashMap<String, String>();
        for (int i = 1; i <= 10; ++i) {
            mp.put("content", "orzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorzorz");
            mp.put("time", "2021-6-5 23:27:17");
            mp.put("posterID", "2019201408");
            content.add(mp);
        }
        ReadMoreButton.addActionListener(new ReadMore(postID, content));

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
        tabPlacementLabel.setText("论坛");
        tabPlacementLabel.setFont(UiConsts.FONT_TITLE0);
        panelTitle.add(tabPlacementLabel, "cell 0 0");

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
        add(jsp, "cell 0 1");
    }
}
