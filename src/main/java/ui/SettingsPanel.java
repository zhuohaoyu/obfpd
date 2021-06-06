package main.java.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPanel extends JPanel {
    public SettingsPanel(){
        initialize() ;
        addComponent() ;
        addListener() ;
    }

    void initialize(){
        this.setLayout( new MigLayout(
                "",
                "[grow,fill]",
                "[min!]20[grow,fill]"
        ) ) ;
    }

    void addComponent(){
        this.add( getUpPanel() , "wrap" ) ;
        this.add( getCenterPanel() , "grow" ) ;
//        this.add( getDownPanel() , BorderLayout.SOUTH ) ;
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , 15 ) ) ;
        JPanel panelUp = new JPanel( new GridLayout( 2 , 1 ) ) ;

        JLabel lableTitle = new JLabel( "设置" ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle ) ;

        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( UiConsts.INF_WIDTH , 20 ) ) ;
        panelUp.add( sepline ) ;
        panelUp0.add( panelUp ) ;
        return panelUp0 ;
    }

    private JPanel getCenterPanel(){
        JPanel panelCenter = new JPanel() ;
        JButton jb1 = new JButton("夜间模式");
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel( new FlatDarculaLaf() ) ;
                    FlatLaf.updateUI();
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                }
            }
        });
        JButton jb2 = new JButton("日间模式");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel( new FlatArcOrangeIJTheme() ) ;
//                    SwingUtilities.invokeLater(() -> App.frame.repaint());
                    FlatLaf.updateUI();
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                }
            }
        });
        panelCenter.setLayout(new MigLayout(
                "",
                "[grow,shrink,fill][grow,shrink,fill]",
                "[]"
        ));
        panelCenter.add(jb1, "growx");
        panelCenter.add(jb2, "growx,wrap");
        return panelCenter ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( ) ;
        return panelDown ;
    }

    void addListener(){

    }
}
