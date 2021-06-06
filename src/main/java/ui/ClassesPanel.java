package main.java.ui;

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


    private void getCenterPanel(){
        JPanel panelCenter0 = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , 0 ) ) ;
        JPanel panelCenter = new JPanel( new GridLayout( 1 , 3 ) ) ;
        UIManager.put( "TabbedPane.tabWidth", 32 );
        {
            classTab = new JTabbedPane() ;
            classTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            classTab.setTabPlacement( JTabbedPane.LEFT ) ;
            classTab.setFont( UiConsts.FONT_NORMAL ) ;
            classTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 200 ) ;
            classTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 200 ) ;
            this.add(classTab);
            homeworkTab = new JTabbedPane( ) ;
            homeworkTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            homeworkTab.setTabPlacement( JTabbedPane.LEFT ) ;
            homeworkTab.setFont( UiConsts.FONT_NORMAL );
            homeworkTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 200 ) ;
            homeworkTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 200 ) ;
            this.add(homeworkTab);
        }
        homeworkDetailPane = new JPanel();
        homeworkDetailPane.setLayout(
                new MigLayout(
                        "ltr,insets 0,hidemode 0",
                        "[fill,grow,shrink]",
                        "[][fill,grow][]"
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
        JButton jb2 = new JButton("真可爱");
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
        HashMap<String, OBECourse> hmp = student.getCourses();
        for(HashMap.Entry<String, OBECourse> ent: hmp.entrySet()) {
            OBECourse curCourse = ent.getValue();
            classTab.addTab(curCourse.getCourseName(), null);
        }
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
                HashMap<String, OBECourse> curCourses = student.getCourses();
                currentSelectedCourse = curCourses.get(chosedClass);
                HashMap<String, OBEHomework> curHws = currentSelectedCourse.getHomework();
                homeworkTabinit = false;
                chosedHomeworkId = -1 ;
                homeworkTab.removeAll();
                homeworkTabinit = true;
                classTab.setComponentAt(chosedClassId, homeworkTab);
                ImageIcon icon = new ImageIcon("images/middle.gif","this is a caption");

                for(HashMap.Entry<String, OBEHomework> ent: curHws.entrySet()) {
                    homeworkTab.add(ent.getValue().getTitle(), null);
                }
            }
        });

        homeworkTab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (homeworkTabinit) {
                    if (chosedHomeworkId >= 0) homeworkTab.setComponentAt(chosedHomeworkId, null);
                    chosedHomeworkId = homeworkTab.getSelectedIndex();
                    chosedHomework = homeworkTab.getTitleAt(chosedHomeworkId);
                    currentSelectedHomework = currentSelectedCourse.getHomework().get(chosedHomework);
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
