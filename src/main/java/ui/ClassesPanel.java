package main.java.ui;

import main.java.ui.mycompo.TimeRefresh;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassesPanel extends JPanel {
    JList homeworkList ;
    DefaultListModel homeworkLSM ;
    JScrollPane listScroll ;
    public ClassesPanel(){
        initialize() ;
        addComponent() ;
    }

    private void initialize(){
        this.setLayout( new BorderLayout() ) ;
    }

    private void updateHomework(){
        homeworkLSM.clear() ;
        String examplehw[] = {"homework1","homework2" } ;

        for( int i = 0 ; i < examplehw.length ; i ++ ){
            homeworkLSM.add( i , examplehw[i] ) ;
        }
        repaint() ;
    }

    private void addComponent(){
        this.add( getUpPanel() , BorderLayout.NORTH ) ;
        this.add( getCenterPanel() , BorderLayout.CENTER ) ;
        this.add( getDownPanel() , BorderLayout.SOUTH ) ;
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , 15 ) ) ;
        JPanel panelUp = new JPanel( new GridLayout( 2 , 1 ) ) ;

        JLabel lableTitle = new JLabel( "课程中心" ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle ) ;

        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( 700 , 20 ) ) ;
        panelUp.add( sepline ) ;
        panelUp0.add( panelUp ) ;
        return panelUp0 ;
    }

    private JPanel getCenterPanel(){
        JPanel panelCenter = new JPanel() ;
        panelCenter.setLayout( new GridLayout( 3 , 1 ) ) ;

        homeworkLSM = new DefaultListModel() ;
        homeworkList = new JList( homeworkLSM ) ;
        listScroll = new JScrollPane( homeworkList ) ;
        this.add( listScroll ) ;

        updateHomework();

        return panelCenter ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( new GridLayout( 1 , 2 ) ) ;


        return panelDown ;
    }

    public void setContent(){

    }
}
