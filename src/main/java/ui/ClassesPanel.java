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

public class ClassesPanel extends JPanel {
    JTabbedPane classTab , homeworkTab ;
    JTable fileTable ;
    JCheckBox switchHomeworkAutoSubmit ;
    JPanel homeworkDetailPane;
    int chosedClassId ;
    int chosedHomeworkId ;
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
        fileTable = new JTable( );
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
            switchHomeworkAutoSubmit = new JCheckBox( "当前作业自动提交" ) ;
        }
        homeworkDetailPane = new JPanel();
        homeworkDetailPane.setLayout(
                new MigLayout(
                        "ltr,insets 0,hidemode 0",
                        "[fill,grow,shrink]",
                        "[][][][fill,grow][fill,grow][][]"
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

    public String packSelectedItems(String path) {
        TableModel mdl = fileTable.getModel();
        int rowCount = mdl.getRowCount();
        ZipOutputStream zos;
        String curHwPath = currentSelectedHomework.getLocalPath();
        Path dataPath;
        Pattern illegalFilePat = Pattern.compile("[\\\\/:*?\"<>| ]");
        String fixedHwName = illegalFilePat.matcher(currentSelectedHomework.getTitle()).replaceAll("");

        String zipFileName = App.student.getUsername() + "-" + fixedHwName + ".zip";

        if(path == null || path.length() < 1) {
            dataPath = Paths.get( App.settingsPanel.nowWorkPath , "OBFPDdata", "submit", zipFileName);
            Path submitDir = Paths.get(  App.settingsPanel.nowWorkPath , "OBFPDdata", "submit");
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
                if(selected) {
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

    public void resetHomeworkDetailPanel() {

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
        l1.setLineWrap(true);

        l1.setEditable(false);
        l1.setName("作业描述");

        homeworkDetailPane.add(l1, "span,growx,growy,wmin 100");
        homeworkDetailPane.setVisible(true);
        Object[][] curF = getCurrentHomeworkLocalData();
        DefaultTableModel tmodel = new DefaultTableModel(
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
        } ;
        fileTable.setModel( tmodel );
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        if( currentSelectedHomework != null ) {
            int rowCount = tmodel.getRowCount() ;
            Map<String,Boolean> tselected = currentSelectedHomework.getUploadSelected() ;
            for (int i = 0; i < rowCount; i++) {
                Boolean astatus = tselected.get( tmodel.getValueAt( i , 0 ) ) ;
                if( astatus != null )
                    tmodel.setValueAt( astatus , i , 2 ) ;
            }
        }

        JScrollPane jsp = new JScrollPane(fileTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        homeworkDetailPane.add(jsp, "span,growx,growy,wmin 100");
        homeworkDetailPane.add( switchHomeworkAutoSubmit , "wrap,wmin 50" ) ;

        if(currentSelectedHomework.getStatus() == 1) {
            JButton jb1 = new JButton("提交作业");
            jb1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TableModel tmodel = fileTable.getModel() ;
                    int rowCount = tmodel.getRowCount() ;
                    int fileCnt = 0 ;
                    for( int i = 0 ; i < rowCount ; i ++ ){
                        if ((boolean) tmodel.getValueAt(i, 2)) {
                            fileCnt++;
                        }
                    }
                    if( fileCnt == 0 ){
                        int vopt = JOptionPane.showConfirmDialog( null ,"没有选择任何文件哎！要交空压缩包吗？", "贴心的提示" , JOptionPane.YES_NO_OPTION) ;
                        if( vopt == JOptionPane.NO_OPTION ) return ;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String packed = packSelectedItems( "" );
                            Boolean ret = App.student.uploadHomework(packed, currentSelectedCourse.getCourseID(), currentSelectedHomework.getId());
                            System.out.println("RES:" + ret.toString());
                            if(ret) {
                                System.out.println("UPLOAD SUCCESS");
                                currentSelectedHomework.setStatus(0);
                                System.out.println(currentSelectedHomework.getDescription());
                                System.out.println(currentSelectedHomework.getDeadLine());
                                currentSelectedHomework.writeInUPDTimeLog();
                                currentSelectedHomework.resetIsUploadSeletedChanged( );
                                SwingUtilities.invokeLater(() -> {
                                    homeworkTab.setIconAt(chosedHomeworkId, checkSVGIcon);
                                    resetHomeworkDetailPanel();
                                    JOptionPane.showMessageDialog( null , "成功提交" , "好起来了" , JOptionPane.INFORMATION_MESSAGE );
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
                    TableModel tmodel = fileTable.getModel() ;
                    int rowCount = tmodel.getRowCount() ;
                    int fileCnt = 0 ;
                    for( int i = 0 ; i < rowCount ; i ++ ){
                        if ((boolean) tmodel.getValueAt(i, 2)) {
                            fileCnt++;
                        }
                    }
                    if( fileCnt == 0 ){
                        int vopt = JOptionPane.showConfirmDialog( null ,"没有选择任何文件哎！要交空压缩包吗？", "贴心的提示" , 0 ) ;
                        if( vopt == JOptionPane.NO_OPTION ) return ;
                    }
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
                                currentSelectedHomework.writeInUPDTimeLog();
                                currentSelectedHomework.resetIsUploadSeletedChanged( );
                                SwingUtilities.invokeLater(() -> {
                                    homeworkTab.setIconAt(chosedHomeworkId, checkSVGIcon);
                                    resetHomeworkDetailPanel();
                                    JOptionPane.showMessageDialog( null , "成功提交" , "好起来了" , JOptionPane.INFORMATION_MESSAGE );
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
                ArrayList<OBEAttachment> attachments = currentSelectedHomework.getAttachments() ;
                if( attachments.size() == 0 ) {
                    JOptionPane.showMessageDialog( null , "此作业无附件" , "坏了" , JOptionPane.INFORMATION_MESSAGE );
                    return;
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

                new Thread(() -> {
                    pbarFrame.init( attachments.size() ) ;
                    pbarFrame.showIt() ;
                    Map<String,String> cookie = App.student.getCookie() ;
                    String path = source.getPath() + "\\" ;
                    for( int i = 0 , lim = attachments.size() ; i < lim ; i ++ ){
                        OBEAttachment attach = attachments.get( i ) ;
                        String filename = attach.getName() ;
                        int cnt = 0 ;
                        pbarFrame.setNowHint( "正在下载：" + filename ) ;
                        while ( !currentSelectedHomework.downloadHomeworkAttachment( cookie , path , filename , Integer.toString( attach.getId() ) , filename ) ) {
                            cnt++;
                            System.out.println("try " + i + " failed");
                            if (cnt > 3) {
                                break;
                            }
                        }
                        pbarFrame.setVal( i + 1 ) ;
                    }
                }).start();
            }
        });

        homeworkDetailPane.add(jb2,"wrap");
        homeworkDetailPane.setVisible(true);
    }

    public void setContent(){
        chosedClassId = -1 ;
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
                ArrayList<OBECourse> curCourses = student.getCourses();
                currentSelectedCourse = curCourses.get( chosedClassId );
                System.out.println( currentSelectedCourse.getCourseName() ) ;
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
                chosedHomeworkId = homeworkTab.getSelectedIndex();
                currentSelectedHomework = currentSelectedCourse.getHomework().get(chosedHomeworkId);
                System.out.println( currentSelectedHomework.getTitle() ) ;
                resetHomeworkDetailPanel();
                homeworkDetailPane.setVisible(true);
                switchHomeworkAutoSubmit.setSelected( currentSelectedHomework.getAllowAutoSubmit() ) ;
            }
        });

        switchHomeworkAutoSubmit.addActionListener(e -> currentSelectedHomework.setAllowAutoSubmit( switchHomeworkAutoSubmit.isSelected() ));

        fileTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Map<String,Boolean> nowSelected = new HashMap<String ,Boolean>() ;
                TableModel tmodel = fileTable.getModel() ;
                int rowCount = tmodel.getRowCount() ;
                boolean isChanged = false ;
                for( int i = 0 ; i < rowCount ; i ++ ){
                    String nowname = (String) tmodel.getValueAt( i , 0 ) ;
                    if( (Boolean) tmodel.getValueAt( i , 2 ) ) {
                        nowSelected.put( nowname , true);
                        if( currentSelectedHomework.getUploadSelected().get( nowname ) == null )
                            isChanged = true ;
                    } else {
                        if( currentSelectedHomework.getUploadSelected().get( nowname ) != null )
                            isChanged = true ;
                    }
                }
                currentSelectedHomework.setIsUploadSeletedChanged() ;
                currentSelectedHomework.writeInChosedFileList( nowSelected ) ;
            }
            @Override
            public void mousePressed(MouseEvent e) { }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });
    }
}
