package main.java.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForumPanel extends JPanel {
    JLabel tabPlacementLabel = null;
    JPanel panelTitle = null;
    JPanel panelContent = null;

    private class ReadMore implements ActionListener {
        String postID = null;
        public ReadMore(String __postID) {
            postID = __postID;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame Reply = new JFrame("Forum");
            Reply.setVisible(true);
        }
    }

    public ForumPanel(){
        initialize() ;
        addComponent() ;
        addListener() ;
    }

    private JPanel createPOST(String title, String user, String postID) {
        JPanel panel = new JPanel();
        panel = new JPanel();
        panel.setName("panel");
        panel.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                "[grow,fill]para",
                "[]5[]"));
        JTextArea jta = new JTextArea();
        jta.setLineWrap(true);
        jta.setEnabled(false);
        jta.setText("Title: \n" + title + ".\n\nUser:  " + user);
        jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
        JScrollPane jsp = new JScrollPane(  jta );
        panel.add(jsp, "cell 0 0");
        JButton ReadMoreButton = new JButton();
        ReadMoreButton.setText("Read More");
        ReadMoreButton.setSelected(true);
        ReadMoreButton.setFont(ReadMoreButton.getFont().deriveFont(ReadMoreButton.getFont().getSize() + 0f));
        ReadMoreButton.setName("ReadMoreButton");
        panel.add(ReadMoreButton, "cell 0 1");

        ReadMoreButton.addActionListener(new ReadMore(postID));
        return panel;
    }

    private void initialize(){
        tabPlacementLabel = new JLabel();

        setName("this");
        setLayout( new MigLayout(
                "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ", hidemode 3",
                "[grow,fill]para",
                "[][]"));

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
        jsp.setBorder(null);
        add(jsp, "cell 0 1");
    }

    void addComponent(){

    }

    void addListener(){

    }
}
