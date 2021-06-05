package main.java.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ForumPanel extends JPanel {
    JLabel tabPlacementLabel = null;
    JPanel panel1 = null;
    public ForumPanel(){
        initialize() ;
        addComponent() ;
        addListener() ;
    }

    private JPanel createPOST(String title, String user) {
        JPanel panel = new JPanel();
        panel = new JPanel();
        panel.setName("panel2");
        panel.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                // columns
                "[grow,fill]para",
                // rows
                "[]" + "[]5[]5[]5[]5[]"));
        JTextArea jta = new JTextArea();
        jta.setLineWrap(true);
        jta.setEnabled(false);
        jta.setText("Title: \nsrowzworz111111111111111111111111111111111111111111111111111111.\n\nUser:  YMJ");
        jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
        JScrollPane lsp = new JScrollPane(  jta );
        panel.add(lsp, "cell 0 0");
        JButton topPlacementButton = new JButton();
        topPlacementButton.setText("Read More");
        topPlacementButton.setSelected(true);
        topPlacementButton.setFont(topPlacementButton.getFont().deriveFont(topPlacementButton.getFont().getSize() + 0f));
        topPlacementButton.setName("topPlacementButton");
        panel.add(topPlacementButton, "cell 0 1");
        return panel;
    }

    private void initialize(){
        tabPlacementLabel = new JLabel();

        setName("this");
        setLayout( new MigLayout(
                "insets 20, hidemode 3",
                "[grow,fill]para",
                "[]" + "[]"));

        panel1 = new JPanel();
        panel1.setName("panel1");
        panel1.setLayout(new MigLayout(
                "insets 0 0 10 0,hidemode 3",
                // columns
                "[grow, fill]para",
                // rows
                "[grow, fill]para"));
        tabPlacementLabel.setText("论坛");
        tabPlacementLabel.setFont(tabPlacementLabel.getFont().deriveFont(tabPlacementLabel.getFont().getSize() + 20f));
        panel1.add(tabPlacementLabel, "cell 0 0");

        add(panel1, "cell 0 0");
        for (int i = 1; i <= 3; ++i) {
            add(createPOST("flkjaskldjfikljashdkjlfhasjkldfhjklashdfjklahsdjklfhjklashdfjklhasdjklfhjklasehdfjklhskl",
                    "lkfasjsdkljfaklsdjflasdkjfklasjdfljasdlfjkaslkdjflkasd"),
                    "cell 0 " + Integer.toString(i));
        }
    }

    void addComponent(){

    }

    void addListener(){

    }
}
