package main.java;

import javax.swing.* ;
import java.awt.* ;

import main.java.ui.* ;

public class App {
    private JFrame frame ;
    private JPanel mainPanel ;
    private TestPanel testPanel ;

    public static void main( String[] args ){
        EventQueue.invokeLater( ()->{
            try {
                App window = new App() ;
                window.frame.setVisible( true ) ;
            } catch ( Exception e ){
                e.printStackTrace() ;
            }
        } ) ;
    }

    App(){
        initialize() ;
        addComponent() ;
    }

    private void initialize(){
        frame = new JFrame() ;
    }

    private void addComponent(){
        mainPanel = new JPanel() ;
        testPanel = new TestPanel() ;
        mainPanel.add( testPanel ) ;
        frame.add( mainPanel ) ;
    }
}
