package main.java.ui;


import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.* ;

public class ToolbarPanel extends JPanel{
    private static JButton buttonOverall ;
    private static JButton buttonClasses ;
    private static JButton buttonSettings ;

    public ToolbarPanel(){
        initialize() ;
        addButton() ;
        addListener() ;
    }

    private void initialize(){
        Dimension preferredSize = new Dimension( 48 , UiConsts.MAIN_WINDOW_HEIGHT ) ;
        this.setPreferredSize( preferredSize ) ;
        this.setMaximumSize( preferredSize ) ;
        this.setMinimumSize( preferredSize ) ;
        this.setLayout( new GridLayout( 2 , 1 ) ) ;
    }

    private void addButton(){

    }

    private void addListener(){
        JPanel panelUp = new JPanel() ;
        panelUp.setLayout( new FlowLayout( FlowLayout.CENTER , 0 , 4 ) ) ;
        JPanel panelDown = new JPanel() ;
        panelDown.setLayout( new BorderLayout( 0 , 0 ) ) ;
        
        buttonOverall = new JButton() ;

        buttonClasses = new JButton() ;

        buttonSettings = new JButton() ;

        panelDown.add( buttonSettings , BorderLayout.SOUTH ) ;
        this.add( panelUp ) ;
        this.add( panelDown ) ;
    }

    
}
