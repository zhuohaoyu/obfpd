package main.java.ui;

import main.java.App;
import main.java.ui.mycompo.TimeRefresh ;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class HomePanel extends Panel {
    JLabel homeworktot , homework3d , homework1d , homeworktle ;
    JLabel lableTitle ;
    JPanel updatePostPanel ;
    public HomePanel(){
        initialize() ;
        addComponent() ;
        setContent() ;
    }

    private void initialize(){
        this.setLayout( new MigLayout(
                "insets " + Integer.toString(UiConsts.MAIN_H_GAP) + ",hidemode 3",
                "[grow,fill]",
                "[][grow,fill][]"
        )) ;
    }

    private void addComponent(){
        this.add( getUpPanel() ,  "cell 0 0,growx" ) ;
        this.add( getCenterPanel() , "cell 0 1,alignx left,growx,growy" ) ;
        this.add( getDownPanel() , "cell 0 2" ) ;
    }

    private JPanel getUpPanel(){
        JPanel panelUp = new JPanel( new MigLayout(
                "",
                "[grow,fill]para",
                "[][][]"
        )) ;
        lableTitle = new JLabel( ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle , "cell 0 0") ;

        TimeRefresh lableTime = new TimeRefresh() ;
        lableTime.setFont( UiConsts.FONT_TITLE2 );
        panelUp.add( lableTime , "cell 0 1") ;

        JSeparator sepline = new JSeparator() ;
        panelUp.add( sepline , "cell 0 2" ) ;
        return panelUp ;
    }

    private JPanel getCenterPanel(){
        JPanel panelCenter = new JPanel( new MigLayout(
                "inset 0, hidemode 3",
                "[]40[grow,fill]para",
                "[]"
        )) ;

        {
            JPanel panelCenterLeft = new JPanel( new MigLayout(
                  "inset 0, hidemode 3",
                  "[]",
                  "[]20[]20[]20[]"
            ));
            homeworktot = new JLabel();
            homework3d = new JLabel();
            homework1d = new JLabel();
            homeworktle = new JLabel() ;
            homeworktot.setFont(UiConsts.FONT_TITLE2);
            homework3d.setFont(UiConsts.FONT_TITLE2);
            homework3d.setForeground(UiConsts.MIMOSAYELLOW);
            homework1d.setFont(UiConsts.FONT_TITLE2);
            homework1d.setForeground(UiConsts.BRIGHTRED);
            homeworktle.setFont( UiConsts.FONT_TITLE2 ) ;
            homeworktle.setForeground( Color.red ) ;
            panelCenterLeft.add(homeworktot, "cell 0 0");
            panelCenterLeft.add(homework3d, "cell 0 1");
            panelCenterLeft.add(homework1d, "cell 0 2");
            panelCenterLeft.add(homeworktle, "cell 0 3") ;
            panelCenter.add( panelCenterLeft , "cell 0 0,aligny top") ;
        }

        {
            updatePostPanel = new JPanel() ;
            JScrollPane jscr = new JScrollPane( updatePostPanel ) ;
            jscr.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
            jscr.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED ); ;
            jscr.getVerticalScrollBar().setUnitIncrement(16);
            panelCenter.add( jscr , "cell 1 0") ;
        }
        return panelCenter ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( new MigLayout(
                "",
                "[]para",
                "[]"
        ) ) ;
        {
            JPanel panelDown1 = new JPanel( new MigLayout(
                    "",
                    "[]para",
                    "[]"
            )) ;
            JButton openOBE = new JButton();
            openOBE.setText("在浏览器中打开...");
            openOBE.setOpaque(true);
            openOBE.setBackground(getBackground());
            openOBE.setFocusPainted(false);
            openOBE.addActionListener(e -> {
                if (Desktop.isDesktopSupported()) {
                    try {
                        java.net.URI uri = java.net.URI.create("http://obe.ruc.edu.cn/");
                        Desktop dp = Desktop.getDesktop();
                        if (dp.isSupported(Desktop.Action.BROWSE)) {
                            dp.browse(uri);
                        }
                    } catch (java.io.IOException ae) {
                        ae.printStackTrace();
                    }
                }
            });
            panelDown1.add( openOBE , "cell 0 0,alignx left") ;
            panelDown.add( panelDown1 ,"cell 0 0,alignx left") ;
        }
        return panelDown ;
    }

    private JPanel createHWupdate( String time , String courseName , String homeworkName ){
        JPanel panel = new JPanel();
        panel = new JPanel();
        panel.setName("panel");
        panel.setLayout( new MigLayout(
                "insets 0,hidemode 3",
                "[grow,fill]",
                "[grow,fill]"
        ));
        JTextArea jta = new JTextArea();
        jta.setLineWrap(true);
        jta.setEditable(false);
        jta.setText("更新作业: 于" + time + "\n    " + courseName + ": " + homeworkName );
        jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
//        JScrollPane jsp = new JScrollPane( jta );
//        jsp.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        panel.add(jta, "cell 0 0,growx,growy,wmin 100");
        panel.setPreferredSize(new Dimension(200, 120));
        return panel;
    }

    private JPanel createNoHWupdate(){
        JPanel panel = new JPanel();
        panel = new JPanel();
        panel.setName("panel");
        panel.setLayout( new MigLayout(
                "insets 0,hidemode 3",
                "[grow,fill]",
                "[grow,fill]"
        ));
        JTextArea jta = new JTextArea();
        jta.setLineWrap(true);
        jta.setEnabled(false);
        jta.setText("暂无新作业" );
        jta.setFont(jta.getFont().deriveFont(jta.getFont().getSize() + 4f));
        JScrollPane jsp = new JScrollPane( jta );
        jsp.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        panel.add(jsp, "cell 0 0,growx,growy");
        panel.setPreferredSize(new Dimension(2000, 100));
        return panel;
    }

    public void setContent(){
        String nowT = "" ;
        Calendar cal = new GregorianCalendar() ;
        int hour = cal.get( Calendar.HOUR_OF_DAY ) ;
        if( hour <= 5 ) nowT = "夜深了" ;
        else if( hour <= 7 ) nowT = "早上好" ;
        else if( hour <= 10 ) nowT = "上午好" ;
        else if( hour <= 13 ) nowT = "中午好" ;
        else if( hour <= 17 ) nowT = "下午好" ;
        else if( hour <= 22 ) nowT = "晚上好" ;
        else if( hour <= 24 ) nowT = "夜深了" ;

        lableTitle.setText( nowT + ( App.islogin ? ", " + App.student.getName() : "" ) ) ;
        int day = cal.get( Calendar.DAY_OF_YEAR ) ;
        System.out.println( day ) ;
        if( day > 243 ) day = -day + 243 ; // 9.1为基准
        else if( day > 90 ) day = -day + 90 ;

        int le0 = App.student.getDayLimitHomeworkCnt(day , 1).size() ;
        int le1 = App.student.getDayLimitHomeworkCnt(day,1).size() ;
        int le3 = App.student.getDayLimitHomeworkCnt(day,3).size() ;
        int le = App.student.getDayLimitHomeworkCnt(day,999).size() ;
        homeworktot.setText( "待完成的作业：" + Integer.toString( le ) + " 项" ) ;
        if( le3 == 0 ) homework3d.setForeground( null );
        homework3d.setText( "剩余时间不足3天的作业：" + Integer.toString( le3 ) + " 项" ) ;
        if( le1 == 0 ) homework1d.setForeground( null );
        homework1d.setText( "剩余时间不足1天的作业：" + Integer.toString( le1 ) + " 项" ) ;
        if( le0 == 0 ) homeworktle.setForeground( null );
        homeworktle.setText( "超时的作业：" + Integer.toString( le0 ) + " 项" ) ;

        String buf = "[]";
        int total = App.update.size() ;
        System.err.printf( "update list: %d items\n" , total );
        updatePostPanel.removeAll();
        if( total == 0 ){
            updatePostPanel.setLayout( new MigLayout(
                    "inset 0, hidemode 3",
                    "[]",
                    "[]"
            ));
            updatePostPanel.add( createNoHWupdate() ) ;
        } else {
            for (int i = 1; i <= total; ++i)
                buf = buf + "15[]";
            updatePostPanel.setLayout(new MigLayout(
                "insets 0,hidemode 3",
                "[grow,fill]",
                buf
            ));
            for (int i = 0; i < total; i++) {
                updatePostPanel.add(createHWupdate(App.update.get(i).get("time"),
                        App.update.get(i).get("course_name"),
                        App.update.get(i).get("homework_name")
                ), "cell 0 " + Integer.toString(i));
            }
        }
    }

}
