package main.java.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import main.java.App;
import main.java.client.OBEAttachment;
import main.java.client.OBECourse;
import main.java.client.OBEHomework;
import main.java.client.OBEManager;
import main.java.server.Crawler;
import main.java.ui.mycompo.progressBarFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MAXIMUM_TAB_WIDTH;
import static com.formdev.flatlaf.FlatClientProperties.TABBED_PANE_MINIMUM_TAB_WIDTH;

public class DownloadPanel extends JPanel {
    JTabbedPane classTab ;
    JTable fileTable ;
    JButton downloadButton ;
    int chosedClassId ;
    OBECourse currentSelectedCourse ;
    String chosedClass ;
    OBEManager student;
    boolean classTabinit , allSelectChosed ;
    public DownloadPanel(){
        this.student = App.student;
        initialize() ;
        addComponent() ;
        setContent();
        addListener() ;
    }

    private void initialize(){
        MigLayout mgl = new MigLayout(
                "",
                "[][grow,fill]",
                "[min!]20[grow,fill]"
        );
        this.setLayout( mgl ) ;
    }

    private void addComponent(){
        this.add(getUpPanel(), "span");
        this.getCenterPanel();
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , UiConsts.MAIN_H_GAP ) ) ;
        JPanel panelUp = new JPanel( new BorderLayout() ) ;

        JLabel lableTitle = new JLabel( "课件下载" ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle ) ;

        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( UiConsts.INF_WIDTH , 20 ) ) ;
        panelUp.add( sepline , BorderLayout.SOUTH ) ;
        panelUp0.add( panelUp ) ;
        return panelUp0 ;
    }

    private void getCenterPanel(){
        UIManager.put( "TabbedPane.tabWidth", 32 );
        {
            classTab = new JTabbedPane() ;
            classTab.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ) ;
            classTab.setTabPlacement( JTabbedPane.LEFT ) ;
            classTab.setFont( UiConsts.FONT_MENU ) ;
            classTab.putClientProperty( TABBED_PANE_MAXIMUM_TAB_WIDTH , 300 ) ;
            classTab.putClientProperty( TABBED_PANE_MINIMUM_TAB_WIDTH , 300 ) ;
            this.add(classTab);
        }
        JPanel downloadPane = new JPanel();
        downloadPane.setLayout(
                new MigLayout(
                        "",
                        "[fill,grow,shrink]",
                        "[grow,fill][]"
                )
        );
        {
            String[] colnames = {"文件名", "是否下载"};
            DefaultTableModel tmodel = new DefaultTableModel(){
                Class<?>[] columnTypes = new Class<?>[] {
                        String.class , Boolean.class
                };
                boolean[] columnEditable = new boolean[] {
                        false , true
                };
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return columnEditable[columnIndex];
                }
            } ;
            tmodel.setColumnIdentifiers( colnames ) ;
            fileTable = new JTable( tmodel ) ;
            fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            fileTable.setRowHeight( 30 ) ;
            fileTable.setShowGrid( false ) ;
            TableColumnModel tcm = fileTable.getColumnModel() ;
            tcm.getColumn( 0 ).setPreferredWidth( 380 ) ;

            JScrollPane jsp = new JScrollPane(fileTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            downloadPane.add(jsp, "span,growx,growy,wmin 100,wrap");
            downloadButton = new JButton("下载到文件夹");
            downloadPane.add( downloadButton , "wrap") ;
        }
        downloadPane.setVisible(true);
        this.add(downloadPane, "growx");
    }

    public void setContent(){
        chosedClassId = -1 ;
        chosedClass = null ;
        classTabinit = false ;
        classTab.removeAll();
        classTabinit = true ;

        ArrayList<OBECourse> hmp = student.getCourses();
        for (OBECourse curCourse : hmp) {
            classTab.addTab(curCourse.getCourseName(), null);
        }
    }


    public void addListener(){
        classTab.addChangeListener(e -> {
            if (classTabinit) {
                chosedClassId = classTab.getSelectedIndex();
                chosedClass = classTab.getTitleAt(chosedClassId);
                System.out.println(chosedClass);
                ArrayList<OBECourse> curCourses = student.getCourses();
                currentSelectedCourse = curCourses.get(classTab.getSelectedIndex());

                DefaultTableModel tModel = ( DefaultTableModel) fileTable.getModel() ;
                while ( tModel.getRowCount() > 0 ) tModel.removeRow( tModel.getRowCount() - 1 ) ;
                tModel.addRow( new Object[]{ "全部下载" , false } ) ;
                allSelectChosed = false ;
                for ( OBEAttachment attach: currentSelectedCourse.getAttachment() ){
                    tModel.addRow( new Object[] { attach.getName() , false } ) ;
                }
            }
        });

        fileTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TableModel tmodel = fileTable.getModel() ;
                boolean nowAllSelectedChosed = ( boolean ) tmodel.getValueAt( 0 , 1  ) ;
                int rowCount = tmodel.getRowCount() ;
                if( nowAllSelectedChosed != allSelectChosed ){
                    allSelectChosed = nowAllSelectedChosed ;
                    for( int i = 0 ; i < rowCount ; i ++ ){
                        tmodel.setValueAt( allSelectChosed , i , 1 ) ;
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });

        downloadButton.addActionListener( e->{
            TableModel tmodel = fileTable.getModel() ;
            int rowCount = tmodel.getRowCount() ;
            int fileCnt = 0 ;
            for( int i = 1 ; i < rowCount ; i ++ ){
                if ((boolean) tmodel.getValueAt(i, 1)) {
                    fileCnt++;
                }
            }
            if( fileCnt == 0 ){
                JOptionPane.showMessageDialog( null ,"未选择要下载的文件！","摸不着头脑", JOptionPane.ERROR_MESSAGE) ;
                return ;
            }

            JFileChooser jfc = new JFileChooser(System.getProperty("user.dir") ) ;
            jfc.setFileHidingEnabled( true ) ;
            jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ) ;

            int vsave = jfc.showDialog( new JLabel( ) , "选择保存位置" ) ;
            File source ;
            if( vsave == JFileChooser.APPROVE_OPTION){
                source = jfc.getSelectedFile() ;
                System.out.printf( "%s\n" , source.getPath() ) ;
                if(!source.exists()){
                    JOptionPane.showMessageDialog( null ,"没有找到这个文件","错误", JOptionPane.ERROR_MESSAGE) ;
                    return ;
                }
            } else {
                return ;
            }

            final int tfileCnt = fileCnt ;
            new Thread(() -> {
                progressBarFrame pbar = new progressBarFrame( "下载附件中..." ) ;
                pbar.showIt() ;
                pbar.init( tfileCnt );
                for( int i = 1 ; i < rowCount ; i ++ ){
                    pbar.setNowHint( "正在下载：" + currentSelectedCourse.getAttachment().get(i-1).getName() );
                    if( (boolean) tmodel.getValueAt( i , 1 ) ){
                        currentSelectedCourse.getAttachment().get(i-1).downloadTo( App.student.getCookie() , source.getPath() + "\\" ) ;
                    }
                    pbar.setVal( i ) ;
                }
                pbar.deleteIt();
            }).start();
        });
    }
}
