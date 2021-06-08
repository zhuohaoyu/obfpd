package main.java.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import main.java.App;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SettingsPanel extends JPanel {
    JButton lightModeButton , darkModeButton ;
    JButton tempPathchooser , workPathchooser;
    JCheckBox autoSubmitSwitch ;
    JTextArea tempPathLabel , workPathLabel ;
    public String nowTempPath , nowWorkPath ;
    public SettingsPanel(){
        initialize() ;
        addComponent() ;
        addListener() ;
    }

    void initialize() {
        File nowFd = new File( System.getProperty("user.dir" ) ) ;
        File configFile = new File("tempPathconfig.txt");
        File aimFd = new File( nowFd , "temp" ) ;
        if(!configFile.exists()) {
            aimFd.mkdir() ;
            nowTempPath = aimFd.getPath() ;
            try {
                var osw = new OutputStreamWriter(new FileOutputStream("./tempPathconfig.txt"), StandardCharsets.UTF_8);
                osw.write( nowTempPath );
                osw.close();
            }
            catch (Exception ae) {
                JOptionPane.showMessageDialog( null ,"读取缓存路径失败","坏了", JOptionPane.ERROR_MESSAGE ) ;
                return ;
            }
        } else {
            try {
                var fin = new BufferedReader(new FileReader("tempPathconfig.txt"));
                nowTempPath = fin.readLine();
            } catch (IOException e) {
                aimFd.mkdir() ;
                nowTempPath = aimFd.getPath() ;
                e.printStackTrace();
            }
        }

        configFile = new File( "workPathconfig.txt" ) ;
        if(!configFile.exists()) {
            nowWorkPath = System.getProperty("user.dir") ;
            try {
                var osw = new OutputStreamWriter(new FileOutputStream("./workPathconfig.txt"), StandardCharsets.UTF_8);
                osw.write( nowWorkPath );
                osw.close();
            }
            catch (Exception ae) {
                JOptionPane.showMessageDialog( null ,"读取工作目录失败","坏了", JOptionPane.ERROR_MESSAGE ) ;
                return ;
            }
        } else {
            try {
                var fin = new BufferedReader(new FileReader("workPathconfig.txt"));
                nowWorkPath = fin.readLine();
            } catch (IOException e) {
                nowWorkPath = System.getProperty("user.dir") ;
                e.printStackTrace();
            }
        }

        this.setLayout( new MigLayout(
                "",
                "[grow,fill]",
                "[min!]20[grow,fill][]"
        ) ) ;
    }

    void addComponent(){
        this.add( getUpPanel() , "wrap" ) ;
        this.add( getCenterPanel() , "growx" ) ;
        this.add( getDownPanel() , "growx,growy" ) ;
    }

    private JPanel getUpPanel(){
        JPanel panelUp0 = new JPanel(new FlowLayout( FlowLayout.LEADING , UiConsts.MAIN_H_GAP , UiConsts.MAIN_H_GAP ) ) ;
        JPanel panelUp = new JPanel( new GridLayout( 2 , 1 ) ) ;

        JLabel lableTitle = new JLabel( "设置" ) ;
        lableTitle.setFont( UiConsts.FONT_TITLE0 ) ;
        panelUp.add( lableTitle ) ;

        JSeparator sepline = new JSeparator() ;
        sepline.setPreferredSize( new Dimension( UiConsts.INF_WIDTH , 20 ) ) ;
        panelUp.add( sepline ) ;
        panelUp0.add( panelUp ) ;
        return panelUp0 ;
    }

    private JPanel getCenterPanel(){
        JPanel panelCenter = new JPanel(new MigLayout(
                "",
                "[grow,shrink,fill]",
                "[fill][fill][fill][fill]"
        )) ;
        {
            JPanel panelColorTheme = new JPanel( new MigLayout(
                    "" ,
                    "[grow,shrink,fill][grow,shrink,fill]",
                    "[fill]"
            )) ;
            darkModeButton = new JButton("夜间模式");
            darkModeButton.setFont( UiConsts.FONT_NORMAL );
            panelColorTheme.add(darkModeButton, "cell 0 0,growx");
            lightModeButton = new JButton("日间模式");
            lightModeButton.setFont( UiConsts.FONT_NORMAL ) ;
            panelColorTheme.add(lightModeButton, "cell 1 0,growx,wrap" ) ;
            panelCenter.add( panelColorTheme , "cell 0 0" ) ;
        }
        {
            JPanel panelTempPath = new JPanel( new MigLayout(
                    "",
                    "[][grow]",
                    "[grow]"
            )) ;
            tempPathLabel = new JTextArea("当前缓存目录：" + nowTempPath + "\\"  ) ;
            tempPathLabel.setEditable( false ) ;
            tempPathLabel.setLineWrap( true ) ;
            tempPathLabel.setFont( UiConsts.FONT_NORMAL ) ;
            panelTempPath.add( tempPathLabel , "cell 1 0,growx,wmin 100" ) ;
            tempPathchooser = new JButton( "选择缓存文件夹" ) ;
            tempPathchooser.setFont( UiConsts.FONT_NORMAL ) ;
            panelTempPath.add( tempPathchooser , "cell 0 0" ) ;
            panelCenter.add( panelTempPath , "cell 0 1" ) ;
        }
        {
            JPanel panelTempPath = new JPanel( new MigLayout(
                    "",
                    "[][grow]",
                    "[grow]"
            )) ;
            workPathLabel = new JTextArea("当前工作目录：" + nowWorkPath + "\\"  ) ;
            workPathLabel.setEditable( false ) ;
            workPathLabel.setLineWrap( true ) ;
            workPathLabel.setFont( UiConsts.FONT_NORMAL ) ;
            panelTempPath.add( workPathLabel , "cell 1 0,growx,wmin 100" ) ;
            workPathchooser = new JButton( "选择工作目录" ) ;
            workPathchooser.setFont( UiConsts.FONT_NORMAL ) ;
            panelTempPath.add( workPathchooser , "cell 0 0" ) ;
            panelCenter.add( panelTempPath , "cell 0 2" ) ;
        }
        {
            JPanel panelCheckAutoUpdate = new JPanel( new MigLayout(
                "",
                "[grow]",
                "[grow]"
            )) ;
            autoSubmitSwitch = new JCheckBox( "是否自动提交" ) ;
            autoSubmitSwitch.setSelected( false ) ;
            panelCheckAutoUpdate.add( autoSubmitSwitch , "cell 0 0,wmin 20" ) ;
            panelCenter.add( panelCheckAutoUpdate , "cell 0 3" ) ;
        }
        return panelCenter ;
    }

    private JPanel getDownPanel(){
        JPanel panelDown;
        panelDown = new JPanel( );
        return panelDown ;
    }

    void addListener(){
        lightModeButton.addActionListener(e -> {
            try {
                FlatAnimatedLafChange.showSnapshot();
                UIManager.setLookAndFeel( new FlatArcOrangeIJTheme() ) ;
                FlatLaf.updateUI();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            }
        });
        darkModeButton.addActionListener(e -> {
            try {
                FlatAnimatedLafChange.showSnapshot();
                UIManager.setLookAndFeel( new FlatDarculaLaf() ) ;
                FlatLaf.updateUI();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                unsupportedLookAndFeelException.printStackTrace();
            }
        });
        tempPathchooser.addActionListener( e -> {
            JFileChooser jfc = new JFileChooser(System.getProperty("user.dir") ) ;
            jfc.setFileHidingEnabled( true ) ;
            jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ) ;

            int vsave = jfc.showDialog( new JLabel( ) , "选择缓存路径" ) ;
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
            nowTempPath = source.getPath() ;
            App.student.createTempFolders( source.getPath() );
            tempPathLabel.setText( "当前缓存目录：" + nowTempPath + "\\" ) ;
            try {
                var osw = new OutputStreamWriter(new FileOutputStream("./tempPathconfig.txt"), StandardCharsets.UTF_8);
                osw.write( nowTempPath );
                osw.close();
            }
            catch (Exception ae) {
                JOptionPane.showMessageDialog( null ,"修改缓存路径失败","坏了", JOptionPane.ERROR_MESSAGE ) ;
                return ;
            }
            JOptionPane.showMessageDialog( null ,"成功修改缓存路径","成功", JOptionPane.INFORMATION_MESSAGE ) ;
        });

        workPathchooser.addActionListener( e -> {
            JFileChooser jfc = new JFileChooser(System.getProperty("user.dir") ) ;
            jfc.setFileHidingEnabled( true ) ;
            jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ) ;

            int vsave = jfc.showDialog( new JLabel( ) , "选择工作目录" ) ;
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
            nowWorkPath = source.getPath() ;
            App.student.createDataFolders( source.getPath() ) ;
            workPathLabel.setText( "当前工作目录：" + nowWorkPath + "\\" ) ;
            try {
                var osw = new OutputStreamWriter(new FileOutputStream("./workPathconfig.txt"), StandardCharsets.UTF_8);
                osw.write( nowWorkPath );
                osw.close();
            }
            catch (Exception ae) {
                JOptionPane.showMessageDialog( null ,"修改工作目录失败","坏了", JOptionPane.ERROR_MESSAGE ) ;
                return ;
            }
            JOptionPane.showMessageDialog( null ,"成功工作目录路径","成功", JOptionPane.INFORMATION_MESSAGE ) ;
        });

        autoSubmitSwitch.addActionListener( e -> App.isAutoSubmit = autoSubmitSwitch.isSelected());
    }
    public void setContent(){
    }
}
