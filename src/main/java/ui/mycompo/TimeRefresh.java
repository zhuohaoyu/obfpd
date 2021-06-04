package main.java.ui.mycompo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeRefresh extends JLabel implements ActionListener {

    private Timer timer;
    private SimpleDateFormat df;
    {
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.setText( df.format( new Date() ) ) ;
        timer = new Timer(1000, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent ae) {
        this.setText( df.format( new Date() ) ) ;
    }

}
