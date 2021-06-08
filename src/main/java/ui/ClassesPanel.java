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
    String chosedClass , chosedHomework;
    String[] homeworkList;
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
        this.getCenterPanel();
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , UiConsts.MAIN_H_GAP ) ) ;
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
                    new Thread(new Runnable() {
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
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        homeworkTab.setIconAt(chosedHomeworkId, checkSVGIcon);
                                        resetHomeworkDetailPanel();
                                    }
                                });
                            }
                        }
                    }).start() ;
                }
            });
            homeworkDetailPane.add(jb1);
        }
        else {
            JButton jb1 = new JButton("重新提交");
            jb1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String packed = packSelectedItems("");
                            App.student.deleteHomework(currentSelectedCourse.getCourseID(), currentSelectedHomework.getId());
                            Boolean ret = App.student.uploadHomework(packed, currentSelectedCourse.getCourseID(), currentSelectedHomework.getId());
                            currentSelectedHomework.setStatus(0);
                            System.out.println("RES:" + ret.toString());
                            if(ret) {
                                System.out.println("UPLO    AD SUCCESS");
                                currentSelectedHomework.setStatus(0);
                                System.out.println(currentSelectedHomework.getDescription());
                                System.out.println(currentSelectedHomework.getDeadLine());
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        homeworkTab.setIconAt(chosedHomeworkId, checkSVGIcon);
                                        resetHomeworkDetailPanel();
                                    }
                                });
                            }
                        }
                    }).start() ;
                }
            });
            homeworkDetailPane.add(jb1);
        }

        JButton jb2 = new JButton("下载附件");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBarFrame pbarFrame = new progressBarFrame( "下载附件中" ) ;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<OBEAttachment> attachments = currentSelectedHomework.getAttachments() ;
                        if( attachments.size() == 0 ) {
                            JOptionPane.showMessageDialog( null , "此作业无附件" , "坏了" , JOptionPane.INFORMATION_MESSAGE );
                            return;
                        }
                        pbarFrame.init( attachments.size() ) ;
                        pbarFrame.showIt();
                        Map<String,String> cookie = App.student.getCookie() ;
                        String path = currentSelectedHomework.getLocalPath() + "\\" ;
                        for( int i = 0 , lim = attachments.size() ; i < lim ; i ++ ){
                            OBEAttachment attach = attachments.get( i ) ;
                            String filename = attach.getName() ;
                            int cnt = 0 ;
                            System.out.println( "enter download: " ) ;
                            System.out.println( i ) ;
                            pbarFrame.setNowHint( "正在下载：" + filename ); ;
                            while ( !currentSelectedHomework.downloadHomeworkAttachment( cookie , path , filename , Integer.toString( attach.getId() ) , filename ) ) {
                                cnt++;
                                System.out.println("try " + Integer.toString(i) + " failed");
                                if (cnt > 3) {
                                    break;
                                }
                            }
                            pbarFrame.setVal( i + 1 ) ;
                        }
//                        pbarFrame.deleteIt();
                    }
                }).start();
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

        ArrayList<OBECourse> hmp = student.getCourses();
        for (OBECourse curCourse : hmp) {
            classTab.addTab(curCourse.getCourseName(), null);
        }
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

                for (OBEHomework curh : curHws) {
                    if (curh.getStatus() == 1) {
                        homeworkTab.addTab(curh.getTitle(), errorSVGIcon, null);
                    } else {
                        homeworkTab.addTab(curh.getTitle(), checkSVGIcon, null);
                    }
                }
            }
        });

        homeworkTab.addChangeListener(e -> {
            if (homeworkTabinit) {
                if (chosedHomeworkId >= 0) homeworkTab.setComponentAt(chosedHomeworkId, null);
                chosedHomeworkId = homeworkTab.getSelectedIndex();
                chosedHomework = homeworkTab.getTitleAt(chosedHomeworkId);
                currentSelectedHomework = currentSelectedCourse.getHomework().get(chosedHomeworkId);
                System.out.println(chosedHomework) ;
                resetHomeworkDetailPanel();
                homeworkDetailPane.setVisible(true);
            }
        });
    }
}
