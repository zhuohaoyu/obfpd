package main.java;

import javax.swing.* ;
import java.awt.* ;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.formdev.flatlaf.FlatDarculaLaf;
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
        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf() ) ;
        } catch ( Exception e ){
            System.err.println( "set L&F failed" ) ;
            e.printStackTrace() ;
        }
        frame = new JFrame() ;
        frame.setBounds( UiConsts.MAIN_WINDOW_X,
                UiConsts.MAIN_WINDOW_Y,
                UiConsts.MAIN_WINDOW_WIDTH,
                UiConsts.MAIN_WINDOW_HEIGHT ) ;
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit( 1 ) ;
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private void addComponent(){
        mainPanel = new JPanel() ;
        testPanel = new TestPanel() ;
        mainPanel.add( testPanel ) ;
        frame.add( mainPanel ) ;
    }
}
