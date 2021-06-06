package main.java.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.kitfox.svg.app.beans.SVGIcon;
import main.java.App;
import main.java.client.OBECourse;
import main.java.client.OBEHomework;
import main.java.client.OBEManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MAXIMUM_TAB_WIDTH;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MINIMUM_TAB_WIDTH;

public class ClassesPanel extends JPanel {
    JTabbedPane classTab , homeworkTab ;
    JTable fileTable ;
    JScrollPane scro_file ;
    JPanel homeworkDetailPane;
    int chosedClassId ;
    int chosedHomeworkId ;
    String chosedClass , classList[] ;
    String chosedHomework , homeworkList[] ;
    OBEManager student;
    OBECourse currentSelectedCourse;
    OBEHomework currentSelectedHomework;
    boolean classTabinit , homeworkTabinit ;
    public ClassesPanel(){
        this.student = App.student;
        initialize() ;
        addComponent() ;
        setContent();
        addListener() ;
    }

    private void initialize(){
        MigLayout mgl = new MigLayout(
                "",
                "[grow][grow][grow,fill]",
                "[min!]20[grow,fill]"
        );
        this.setLayout( mgl ) ;
    }

    private void addComponent(){
        this.add(getUpPanel(), "span");
//        this.add( getRightPanel()) ;
//        this.add( getDownPanel()) ;

//        this.add( getCenterPanel()) ;
        this.getCenterPanel();
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , UiConsts.MAIN_EDGE_GAP ) ) ;
        JPanel panelUp = new JPanel( new BorderLayout() ) ;

        JLabel lableTitle = new JLabel( "课程中心" ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle ) ;

        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( UiConsts.INF_WIDTH , 20 ) ) ;
        panelUp.add( sepline , BorderLayout.SOUTH ) ;
        panelUp0.add( panelUp ) ;
        return panelUp0 ;
    }

    private ImageIcon getScaledImageIcon(String path, int w, int h){
        ImageIcon classIcon = new ImageIcon(path);
        Image srcImg = classIcon.getImage();
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        ImageIcon resizedClassIcon = new ImageIcon(resizedImg);
        return resizedClassIcon;
    }

    private void getCenterPanel(){
        JPanel panelCenter0 = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , 0 ) ) ;
        JPanel panelCenter = new JPanel( new GridLayout( 1 , 3 ) ) ;
        UIManager.put( "TabbedPane.tabWidth", 32 );
        {
            classTab = new JTabbedPane() ;
            classTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            classTab.setTabPlacement( JTabbedPane.LEFT ) ;
            classTab.setFont( UiConsts.FONT_MENU ) ;
            classTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 200 ) ;
            classTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 200 ) ;
            this.add(classTab);
            homeworkTab = new JTabbedPane( ) ;
            homeworkTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            homeworkTab.setTabPlacement( JTabbedPane.LEFT ) ;
            homeworkTab.setFont( UiConsts.FONT_MENU2 );
            homeworkTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 240 ) ;
            homeworkTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 240 ) ;
            this.add(homeworkTab);
        }
        homeworkDetailPane = new JPanel();
        homeworkDetailPane.setLayout(
                new MigLayout(
                        "ltr,insets 0,hidemode 0",
                        "[fill,grow,shrink]",
                        "[][][][fill,grow][]"
                )
        );
        homeworkDetailPane.setVisible(true);
        this.add(homeworkDetailPane, "growx");
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , UiConsts.MAIN_EDGE_GAP  ) ) ;

        return panelDown ;
    }

    private JPanel getRightPanel(){
        JPanel panelRight = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , UiConsts.MAIN_EDGE_GAP  ) ) ;
        return panelRight ;
    }

    private void getHomeworkDetailPanel() {
//        try{

        homeworkDetailPane.setVisible(false);
        JLabel homeworkTitle = new JLabel( currentSelectedHomework.getTitle()) ;
        homeworkTitle.setFont( UiConsts.FONT_TITLE1 ) ;
        homeworkDetailPane.add(homeworkTitle, "span,wrap,wmin 100");

        JLabel homeworkDDL = new JLabel( "截止时间：" + currentSelectedHomework.getDeadLine()) ;
        homeworkDDL.setFont( UiConsts.FONT_TITLE3 ) ;
        homeworkDetailPane.add(homeworkDDL, "span,wrap,wmin 100");

        JLabel homeworkPublishTime = new JLabel( "布置时间："+ currentSelectedHomework.getPublishTime()) ;
        homeworkPublishTime.setFont( UiConsts.FONT_TITLE3 ) ;
        homeworkDetailPane.add(homeworkPublishTime, "span,wrap,wmin 100");

        System.out.println(currentSelectedHomework.getDescription());

        JTextArea l1 = new JTextArea(currentSelectedHomework.getDescription());
        l1.setLineWrap(true);
        l1.setOpaque(false);
        l1.setEditable(false);
        l1.setName("作业描述");

        homeworkDetailPane.add(l1, "span,growx,growy,wmin 100");
        homeworkDetailPane.setVisible(true);
        System.out.println(l1.getSize().height + ", " + l1.getSize().width);
        JButton jb1 = new JButton("汪元森");
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel( new FlatDarculaLaf() ) ;
//                    SwingUtilities.invokeLater(() -> App.frame.repaint());
                    FlatLaf.updateUI();
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                }
            }
        });
        JButton jb2 = new JButton("真可爱");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel( new FlatCyanLightIJTheme() ) ;
//                    SwingUtilities.invokeLater(() -> App.frame.repaint());
                    FlatLaf.updateUI();
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                }
            }
        });
        homeworkDetailPane.add(jb1);
        homeworkDetailPane.add(jb2,"wrap");
//        ret.add(new JLabel(currentSelectedHomework.getDescription()), "wrap");
//        ret.setSize(new Dimension(800, 600));
//        return ret;
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void setContent(){
        chosedClassId = -1 ;
        chosedClass = null ;
        chosedHomework = null ;
        homeworkList = null ;
        classTabinit = false ;
        classTab.removeAll();
        classTabinit = true ;

//        classList = new String[]{"claaaaaaaaaaaaaaaaass1","class2","class3"} ;
        ArrayList<OBECourse> hmp = student.getCourses();
        for(int i = 0; i < hmp.size(); ++i) {
            OBECourse curCourse = hmp.get(i);
            classTab.addTab(curCourse.getCourseName(), null);

        }
//        for(HashMap.Entry<String, OBECourse> ent: hmp.entrySet()) {
//            OBECourse curCourse = ent.getValue();
//            classTab.addTab(curCourse.getCourseName(), null);
//        }
//        for( int i = 0 ; i < hmp.size(); i ++ ){
//            classTab.addTab( classList[i] , null ) ;
//        }
    }

    public void addListener(){
        classTab.addChangeListener(e -> {
            if (classTabinit) {
                if (chosedClassId >= 0) classTab.setComponentAt(chosedClassId, null);
                chosedClassId = classTab.getSelectedIndex();
                chosedClass = classTab.getTitleAt(chosedClassId);
                System.out.println(chosedClass);
                ArrayList<OBECourse> curCourses = student.getCourses();
                currentSelectedCourse = curCourses.get(classTab.getSelectedIndex());
                ArrayList<OBEHomework> curHws = currentSelectedCourse.getHomework();
                homeworkTabinit = false;
                chosedHomeworkId = -1 ;
                homeworkTab.removeAll();
                homeworkTabinit = true;
                classTab.setComponentAt(chosedClassId, homeworkTab);
                FlatSVGIcon checkSVGIcon = new FlatSVGIcon("client/check.svg");
                FlatSVGIcon errorSVGIcon = new FlatSVGIcon("client/error.svg");

                FlatSVGIcon fsi = new FlatSVGIcon("client/check.svg");
                ImageIcon checkedIcon = getScaledImageIcon("./resources/check.png", 28, 28);
                ImageIcon errorIcon = getScaledImageIcon("./resources/error.png", 28, 28);
//                homeworkTab.setTabLayoutPolicy(JTabbedPane.LEFT);
                for(int i = 0; i < curHws.size(); ++i) {
                    OBEHomework curh = curHws.get(i);

                    if(curh.getStatus() == 1) {
                        homeworkTab.addTab(curh.getTitle(), errorSVGIcon, null);
                    }
                    else {
                        homeworkTab.addTab(curh.getTitle(), checkSVGIcon, null);
                    }
                }
//                for(HashMap.Entry<String, OBEHomework> ent: curHws.entrySet()) {
//                    OBEHomework curh = ent.getValue();
//
////                    homeworkTab.addTab(ent.getValue().getTitle(), checkedIcon, null);
//                }
//                homeworkTab.setIconAt(1, checkedIcon);
            }
        });

        homeworkTab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (homeworkTabinit) {
                    if (chosedHomeworkId >= 0) homeworkTab.setComponentAt(chosedHomeworkId, null);
                    chosedHomeworkId = homeworkTab.getSelectedIndex();
                    chosedHomework = homeworkTab.getTitleAt(chosedHomeworkId);
                    currentSelectedHomework = currentSelectedCourse.getHomework().get(chosedHomeworkId);
                    System.out.println(chosedHomework) ;
                    homeworkDetailPane.setVisible(false);
                    homeworkDetailPane.removeAll();
                    getHomeworkDetailPanel();
//                    homeworkDetailPane.add(getHomeworkDetailPanel());
//                    homeworkDetailPane.setPreferredSize(new Dimension(800, 600));
                    homeworkDetailPane.setVisible(true);
//                    homeworkTab.setComponentAt( chosedHomeworkId , scro_file );


                }
            }
        });
    }
}
