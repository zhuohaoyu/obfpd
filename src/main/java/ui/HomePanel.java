package main.java.ui;

import main.java.ui.mycompo.TimeRefresh ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePanel extends Panel {
    JLabel homeworktot , homework3d , homework1d ;
    JLabel pptcnt ;
    public HomePanel(){
        initialize() ;
        addComponent() ;
        setContent() ;
    }

    private void initialize(){
        this.setLayout( new BorderLayout() ) ;
    }

    private void addComponent(){
        this.add( getUpPanel() , BorderLayout.NORTH ) ;
        this.add( getCenterPanel() , BorderLayout.CENTER ) ;
        this.add( getDownPanel() , BorderLayout.SOUTH ) ;
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , 15 ) ) ;
        JPanel panelUp = new JPanel( new GridLayout( 3 , 1 ) ) ;

        JLabel lableTitle = new JLabel( "欢迎" ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle ) ;

        TimeRefresh lableTime = new TimeRefresh() ;
        lableTime.setFont( UiConsts.FONT_TITLE2 );
        panelUp.add( lableTime ) ;

        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( 700 , 20 ) ) ;
        panelUp.add( sepline ) ;
        panelUp0.add( panelUp ) ;
        return panelUp0 ;
    }

    private JPanel getCenterPanel(){
        JPanel panelCenter = new JPanel() ;
        panelCenter.setLayout( new GridLayout( 3 , 1 ) ) ;

        // homework info
        JPanel panel1 = new JPanel(  new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , 0 ) ) ;
            homeworktot = new JLabel() ;
            homework3d = new JLabel() ;
            homework1d = new JLabel() ;
            homeworktot.setFont(UiConsts.FONT_NORMAL) ;
            homeworktot.setPreferredSize( new Dimension( UiConsts.MAIN_CONTENT_WIDTH , 30 ) ) ;
            homework3d.setFont(UiConsts.FONT_NORMAL );
            homework3d.setForeground( UiConsts.MIMOSAYELLOW ) ;
            homework3d.setPreferredSize( new Dimension( UiConsts.MAIN_CONTENT_WIDTH , 30 ) ) ;
            homework1d.setFont(UiConsts.FONT_NORMAL );
            homework1d.setForeground( UiConsts.BRIGHTRED ) ;
            homework1d.setPreferredSize( new Dimension( UiConsts.MAIN_CONTENT_WIDTH , 30 ) ) ;
            panel1.add( homeworktot ) ;
            panel1.add( homework3d ) ;
            panel1.add( homework1d ) ;

        // ppt info
        JPanel panel2 = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , 30 ) ) ;
            pptcnt = new JLabel() ;
            pptcnt.setFont(UiConsts.FONT_NORMAL );
            panel2.add( pptcnt ) ;

        panelCenter.add( panel1 ) ;
        panelCenter.add( panel2 ) ;

        return panelCenter ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( new GridLayout( 1 , 2 ) ) ;
        JPanel panelGrid1 = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , 15 ) ) ;
            JButton openOBE = new JButton() ;
            openOBE.setText( "在浏览器中打开..." ) ;
            openOBE.setOpaque( true ) ;
            openOBE.setBackground( getBackground() ) ;
            openOBE.setFocusPainted( false );
            openOBE.addActionListener(e -> {
                if(Desktop.isDesktopSupported()){
                    try {
                        java.net.URI uri = java.net.URI.create("http://obe.ruc.edu.cn/");
                        Desktop dp = Desktop.getDesktop();
                        if(dp.isSupported(Desktop.Action.BROWSE)){
                            dp.browse(uri);
                        }
                    } catch ( java.io.IOException ae ) {
                        ae.printStackTrace();
                    }
                }
            });
            panelGrid1.add( openOBE ) ;

        panelDown.add( panelGrid1 ) ;
        return panelDown ;
    }

    public void setContent(){
        homeworktot.setText( "待完成的作业:" + "" + " 项" ) ;
        homework3d.setText( "剩余时间不足 3 天的作业:" + "" + " 项" ) ;
        homework1d.setText( "剩余时间不足 1 天的作业:" + "" + " 项" ) ;
        pptcnt.setText( "最近一周的新课件:" + "" + " 项");
    }

}
