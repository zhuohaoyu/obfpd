package main.java.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MAXIMUM_TAB_WIDTH;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MINIMUM_TAB_WIDTH;

public class ClassesPanel extends JPanel {
    JTabbedPane classTab , homeworkTab ;
    JTable fileTable ;
    JScrollPane scro_file ;
    int chosedClassId ;
    int chosedHomeworkId ;
    String chosedClass , classList[] ;
    String chosedHomework , homeworkList[] ;
    boolean classTabinit , homeworkTabinit ;
    public ClassesPanel(){
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
            classTab = new JTabbedPane() ;
            classTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            classTab.setTabPlacement( JTabbedPane.LEFT ) ;
            classTab.setFont( UiConsts.FONT_NORMAL ) ;
            classTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 150 ) ;
            classTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 150 ) ;
            this.add(classTab);
            homeworkTab = new JTabbedPane( ) ;
            homeworkTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            homeworkTab.setTabPlacement( JTabbedPane.LEFT ) ;
            homeworkTab.setFont( UiConsts.FONT_NORMAL );
            homeworkTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 150 ) ;
            homeworkTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 150 ) ;
            this.add(homeworkTab);
            String colnames[] = { "文件名" , "提交确认" } ;
            DefaultTableModel tmodel = new DefaultTableModel( ) ;
            tmodel.setColumnIdentifiers( colnames );
//            fileTable = new JTable( tmodel ) ;
//            fileTable.getTableHeader().setResizingAllowed(true) ;
//            fileTable.setRowHeight(30) ;
//            scro_file = new JScrollPane( fileTable ) ;
            JTextArea jtatest = new JTextArea("占位jtextarea \n 汪元森好可爱！！！\n 写屎山就不可爱了！！！！！");
            jtatest.setPreferredSize(new Dimension(800, 600));
//            String ts[] = {"?" , "?" } ;
//            tmodel.addRow( ts );
            this.add(jtatest, "growx");

//        panelCenter.add( classTab ) ;
//        panelCenter0.add( panelCenter ) ;
//        return panelCenter0 ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , UiConsts.MAIN_EDGE_GAP  ) ) ;

        return panelDown ;
    }

    private JPanel getRightPanel(){
        JPanel panelRight = new JPanel( new FlowLayout( FlowLayout.LEFT , UiConsts.MAIN_H_GAP , UiConsts.MAIN_EDGE_GAP  ) ) ;
        return panelRight ;
    }

    public void setContent(){
        chosedClassId = -1 ;
        chosedClass = null ;
        chosedHomework = null ;
        homeworkList = null ;
        classTabinit = false ;
        classTab.removeAll();
        classTabinit = true ;

        classList = new String[]{"claaaaaaaaaaaaaaaaass1","class2","class3"} ;
        for( int i = 0 ; i < classList.length ; i ++ ){
            classTab.addTab( classList[i] , null ) ;
        }
    }

    public void addListener(){
        classTab.addChangeListener(e -> {
            if (classTabinit) {
                if (chosedClassId >= 0) classTab.setComponentAt(chosedClassId, null);
                chosedClassId = classTab.getSelectedIndex();
                chosedClass = classTab.getTitleAt(chosedClassId);
                System.out.println(chosedClass);
                homeworkList = new String[]{"homddddddddework1", "homework2", "homework3"};
                if (chosedClassId == 2)
                    homeworkList = new String[]{"qwqwqwqwq", "_(:з)∠)_", "homework3"};
                homeworkTabinit = false;
                chosedHomeworkId = -1 ;
                homeworkTab.removeAll();
                homeworkTabinit = true;
                classTab.setComponentAt(chosedClassId, homeworkTab);
                for (int i = 0; i < homeworkList.length; i++) {
                    homeworkTab.addTab(homeworkList[i], null);
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
                    System.out.println(chosedHomework) ;
                    homeworkTab.setComponentAt( chosedHomeworkId , scro_file );


                }
            }
        });
    }
}
