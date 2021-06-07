package main.java.ui.mycompo;

import main.java.ui.UiConsts;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class progressBarFrame extends JFrame {
    JTextArea nowDownloadHint ;
    JProgressBar progressBar ;
    int maximum ;
    public progressBarFrame( String title ){
        setTitle( title ) ;
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setSize(500 , 200 );
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setAlwaysOnTop( true ) ;

        setLayout(new MigLayout(
                "insets 25,hidemode 3",
                "[]",
                "[]10[]"
        ));

        {
            JPanel panelTitle = new JPanel();
            nowDownloadHint = new JTextArea();
            nowDownloadHint.setLineWrap( true );
//            nowDownloadHint.setEnabled( false ) ;
            nowDownloadHint.setOpaque( false ) ;
            nowDownloadHint.setFont(UiConsts.FONT_MENU2 ) ;
            nowDownloadHint.setPreferredSize( new Dimension( 400 , 20 ) );
            panelTitle.add(nowDownloadHint);
            add(panelTitle, "cell 0 0");
        }

        {
            JPanel panelBar = new JPanel();
            progressBar = new JProgressBar() ;
            progressBar.setPreferredSize( new Dimension( 400 , 25 ) ) ;
            progressBar.setStringPainted( true ) ;
            panelBar.add( progressBar ) ;
            add(panelBar, "cell 0 1");
        }
    }

    public void init( int max_ ){
        maximum = max_ ;
        System.out.printf( "enter progress init:%d" , max_ ) ;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setMaximum( maximum ) ;
                progressBar.setValue( 0 ) ;
            }
        });
    }

    public void setNowHint( String sTitle ){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                nowDownloadHint.setText( sTitle ) ;
            }
        });
    }

    public void setVal( int val ){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue( val ) ;
                progressBar.setString( Integer.toString(val) + "/" + Integer.toString(maximum) ) ;
            }
        });
    }

    public void showIt(){
        this.setVisible( true ) ;
    }

    public void deleteIt(){
        this.dispose() ;
    }
}
