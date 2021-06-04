package main.java.ui;

import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import main.java.ui.*;

public class TestPanel extends JPanel {
    public static JButton homeButton ;
    public TestPanel(){
        initialize() ;
        addComponent() ;
    }

    private void initialize(){

        this.setLayout( new BorderLayout() ) ;
    }

    private void addComponent(){
        homeButton = new JButton() ;
            homeButton.setText( "home?") ;
        this.add( homeButton , BorderLayout.CENTER ) ;
    }
}
