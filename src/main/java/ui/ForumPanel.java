package main.java.ui;

import javax.swing.*;
import java.awt.*;

public class ForumPanel extends JPanel {
    public ForumPanel(){
        initialize() ;
        addComponent() ;
        addListener() ;
    }

    void initialize(){
        this.setLayout( new BorderLayout() ) ;
    }

    void addComponent(){
        this.add( getUpPanel() , BorderLayout.NORTH ) ;
        this.add( getCenterPanel() , BorderLayout.CENTER ) ;
        this.add( getDownPanel() , BorderLayout.SOUTH ) ;
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , 15 ) ) ;
        JPanel panelUp = new JPanel( new GridLayout( 2 , 1 ) ) ;

        JLabel lableTitle = new JLabel( "论坛" ) ;
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

        return panelCenter ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( ) ;
        return panelDown ;
    }

    void addListener(){

    }
}
