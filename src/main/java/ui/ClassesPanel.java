package main.java.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import main.java.App;
import main.java.client.OBECourse;
import main.java.client.OBEHomework;
import main.java.client.OBEManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static final FlatSVGIcon checkSVGIcon = new FlatSVGIcon("client/check.svg");
    private static final FlatSVGIcon errorSVGIcon = new FlatSVGIcon("client/error.svg");
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
                        "[][][][fill,grow][fill,grow][]"
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

    private Object[][] getCurrentHomeworkLocalData() {
        String path = currentSelectedHomework.getLocalPath();
        System.out.println(path);
        File file = new File(path);
        File[] tempList = file.listFiles();
        Object[] ret[] = new Object[tempList.length][];
        for (int i = 0; i < tempList.length; i++) {
            System.out.println(tempList[i].getName());
            Date date = new Date(tempList[i].lastModified());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = formatter.format(date);
            if (tempList[i].isFile()) {
                Object[] col = new Object[] {tempList[i].getName(), strDate, false};
                ret[i] = col;
            }
            if (tempList[i].isDirectory()) {
                System.out.println(tempList[i].getName());
                Object[] col = new Object[] {tempList[i].getName(), strDate, false};
                ret[i] = col;
            }
        }
        return ret;
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    private String packSelectedItems(String path) {
        TableModel mdl = fileTable.getModel();
        int rowCount = mdl.getRowCount();
        ZipOutputStream zos = null ;
        String curHwPath = currentSelectedHomework.getLocalPath();
        Path dataPath = null;
        Pattern illegalFilePat = Pattern.compile("[\\\\/:*?\"<>| ]");
        String fixedHwName = illegalFilePat.matcher(currentSelectedHomework.getTitle()).replaceAll("");

        String zipFileName = App.student.getUsername() + "-" + fixedHwName + ".zip";

        if(path == null || path.length() < 1) {
            dataPath = Paths.get(System.getProperty("user.dir"), "OBFPDdata", "submit", zipFileName);
            Path submitDir = Paths.get(System.getProperty("user.dir"), "OBFPDdata", "submit");
            try{
                Files.createDirectories(submitDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(dataPath);
        }
        else {
            dataPath = Paths.get(path, "OBFPDdata", "submit", zipFileName);
            Path submitDir = Paths.get(path, "OBFPDdata", "submit");
            try{
                Files.createDirectories(submitDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(dataPath);
        }

        try{
            FileOutputStream fos = new FileOutputStream(dataPath.toString());
            zos = new ZipOutputStream(fos);
            for(int i = 0; i < rowCount; ++i) {
                String filename = (String) mdl.getValueAt(i, 0);
                Boolean selected = (Boolean) mdl.getValueAt(i, 2);
                if(selected == true) {
                    System.out.println("ZIP:" + filename);
                    Path curSrcPath = Paths.get(curHwPath, filename);
                    File src = new File(curSrcPath.toString());
                    zipFile(src, src.getName(), zos);
                    }
                }
                zos.close();
                fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataPath.toString();
    }

    private void resetHomeworkDetailPanel() {

        homeworkDetailPane.setVisible(false);
        homeworkDetailPane.removeAll();
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
//        l1.setFont(UiConsts);
        l1.setLineWrap(true);

        l1.setEditable(false);
        l1.setName("作业描述");
//        jsp0.setVisible(true);

        homeworkDetailPane.add(l1, "span,growx,growy,wmin 100");
        homeworkDetailPane.setVisible(true);
        Object[][] curF = getCurrentHomeworkLocalData();
        fileTable = new JTable();
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        fileTable.setModel(new DefaultTableModel(
                curF,
                new String[] {
                        "文件名", "修改时间", "是否上传"
                }
        ) {
            Class<?>[] columnTypes = new Class<?>[] {
                    String.class, String.class, Boolean.class
            };
            boolean[] columnEditable = new boolean[] {
                    false, false, true
            };
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        });

        JScrollPane jsp = new JScrollPane(fileTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        homeworkDetailPane.add(jsp, "span,growx,growy,wmin 100");

        if(currentSelectedHomework.getStatus() == 1) {
            JButton jb1 = new JButton("提交作业");
            jb1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    (new Runnable() {
                        @Override
                        public void run() {
                            String packed = packSelectedItems("");
                            Boolean ret = App.student.uploadHomework(packed, currentSelectedCourse.getCourseID(), currentSelectedHomework.getId());
                            System.out.println("RES:" + ret.toString());
                            if(ret) {
                                System.out.println("UPLOAD SUCCESS");
                                currentSelectedHomework.setStatus(0);
                                System.out.println(currentSelectedHomework.getDescription());
                                System.out.println(currentSelectedHomework.getDeadLine());
                                homeworkTab.setIconAt(chosedHomeworkId, checkSVGIcon);
                                resetHomeworkDetailPanel();
                            }
                        }
                    }).run();
                }
            });
            homeworkDetailPane.add(jb1);
        }
        else {
            JButton jb1 = new JButton("重新提交");
            jb1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    (new Runnable() {
                        @Override
                        public void run() {
                            String packed = packSelectedItems("");
                            App.student.deleteHomework(currentSelectedCourse.getCourseID(), currentSelectedHomework.getId());
                            Boolean ret = App.student.uploadHomework(packed, currentSelectedCourse.getCourseID(), currentSelectedHomework.getId());
                            currentSelectedHomework.setStatus(0);
                            System.out.println("RES:" + ret.toString());
                            if(ret) {
                                System.out.println("UPLOAD SUCCESS");
                                currentSelectedHomework.setStatus(0);
                                System.out.println(currentSelectedHomework.getDescription());
                                System.out.println(currentSelectedHomework.getDeadLine());
                                homeworkTab.setIconAt(chosedHomeworkId, checkSVGIcon);
                                resetHomeworkDetailPanel();
//                                SwingUtilities.invokeLater(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                    }
//                                });
                            }
                        }
                    }).run();
                }
            });
            homeworkDetailPane.add(jb1);
        }

        JButton jb2 = new JButton("下载附件");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        App.student.createDataFolders("");
                    }
                });
            }
        });

        homeworkDetailPane.add(jb2,"wrap");
        homeworkDetailPane.setVisible(true);
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

                    resetHomeworkDetailPanel();
//                    homeworkDetailPane.add(getHomeworkDetailPanel());
//                    homeworkDetailPane.setPreferredSize(new Dimension(800, 600));
                    homeworkDetailPane.setVisible(true);
//                    homeworkTab.setComponentAt( chosedHomeworkId , scro_file );


                }
            }
        });
    }
}
