package main.java;

import javax.swing.* ;
import java.awt.* ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import main.java.client.MyClient;
import main.java.client.OBECourse;
import main.java.client.OBEManager;
import main.java.ui.* ;
import net.miginfocom.swing.MigLayout;

public class App {
    public static boolean isForum = false;
    public static boolean islogin ;
    public static String username ;
    public static String password ;
    public static JFrame frame ;
    public static JPanel mainPanelCenter , mainToolPanel ;

    public static TestPanel testPanel ;
    public static ToolbarPanel toolbarPanel ;
    public static HomePanel homePanel ;
    public static ClassesPanel classesPanel ;
    public static SettingsPanel settingsPanel ;
    public static ForumPanel forumPanel;

    public static OBEManager student;
    public static MyClient myclient;
    public static ArrayList<Map<String, String>> update = new ArrayList<>();

    public static void main( String[] args ){
        EventQueue.invokeLater( ()->{
            try {
                App window = new App() ;
                window.frame.setVisible( true ) ;
            } catch ( Exception e ){
                e.printStackTrace() ;
            }
        } ) ;

    }


    public class Login extends JDialog {
        public JTextField jtaid = null;
        public JPasswordField  jtapw = null;

        public Login() {
            setTitle("登录");
            setModal(true);
            setSize(350, 250);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new MigLayout(
                    "insets 25,hidemode 3",
                    "[grow,fill]para",
                    "[]10[]10[]20[]"
            ));

            {
                JPanel panelTitle = new JPanel();
                JLabel label = new JLabel("登   录");
                label.setFont(UiConsts.FONT_TITLE2);
                panelTitle.add(label);
                add(panelTitle, "cell 0 0");
            }

            {
                JPanel panelID = new JPanel();
                panelID.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[][grow,fill]para",
                        "[grow,fill]"
                ));
                JLabel label = new JLabel("学号：");
                jtaid = new JTextField();
                jtaid.putClientProperty( "JComponent.roundRect", true );
                panelID.add(label, "cell 0 0");
                panelID.add(jtaid, "cell 1 0");
                add(panelID, "cell 0 1");
            }

            {
                JPanel panelPassword = new JPanel();
                panelPassword.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[][grow,fill]para",
                        "[grow,fill]"
                ));
                JLabel label = new JLabel("密码：");
                jtapw = new JPasswordField ();
                jtapw.putClientProperty( "JComponent.roundRect", true );
                panelPassword.add(label, "cell 0 0");
                panelPassword.add(jtapw, "cell 1 0");
                add(panelPassword, "cell 0 2");
            }

            {
                JPanel panelButton = new JPanel();
                panelButton.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[]10[]",
                        "[grow,fill]"
                ));
                JButton jbok = new JButton("确 定");
                jbok.putClientProperty( "JButton.buttonType", "roundRect" );
                JButton jbcancle = new JButton("取 消");
                jbcancle.putClientProperty( "JButton.buttonType", "roundRect" );

                jbok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println( "????" ) ;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                jbok.setEnabled(false);
                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                username = jtaid.getText();
                                password = jtapw.getText();
                                student.setInfo(username, password);
                                islogin = student.doLogin();
                                student.getContent();
                                student.createDataFolders("");
                                System.out.println( "????" ) ;
                                try {
                                    SwingUtilities.invokeAndWait(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (islogin == true) dispose();
                                            else {
                                                JOptionPane.showMessageDialog(null, "账号或者密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                                                jbok.setEnabled(true);
                                            }
                                        }
                                    });
                                } catch ( Exception ae ){ ae.printStackTrace(); System.exit(1);}
                            }
                        }).start(); ;
                    }
                });
                jbcancle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                panelButton.add(jbok, "cell 0 0");
                panelButton.add(jbcancle, "cell 1 0");
                add(panelButton, "cell 0 3,align center,growx 0");
            }
        }

        public void login() {
            this.setVisible(true);
        }
    }

    App(){
        islogin = false;
        student = new OBEManager();
        initialize() ;
        new Login().login();
//        islogin = true;
        if (islogin == false) System.exit(0);
        myclient = new MyClient("username");
        addComponent() ;

        try {
            String ti;
            File configFile = new File("config.txt");
            if(!configFile.exists()) {
                // set default
                ti = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()).toString();
            } else {
                var fin = new BufferedReader(new FileReader("config.txt"));
                ti = fin.readLine();
            }
            System.err.println("time = " + ti);
            ArrayList<OBECourse> cl = student.getCourses();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Task", "queryUPDATE");
            jsonObject.put("Time", ti);
            jsonObject.put("size", Integer.toString(cl.size()));
            int sum = 0;
            for (OBECourse c : cl) {
                jsonObject.put(Integer.toString(sum), c.getCourseID());
                sum += 1;
            }
            myclient.send(jsonObject);
        }
        catch (Exception e) { }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        var osw = new OutputStreamWriter(new FileOutputStream("./config.txt"), "UTF-8");
                        osw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                        osw.close();
                        Thread.sleep(30 * 1000);
                    }
                    catch (Exception e) { }
                }
            }
        }).start();
    }

    private void initialize(){
        try {
            UIManager.setLookAndFeel( new FlatArcOrangeIJTheme() ) ;
            UIManager.put( "TabbedPane.tabAlignment" , "leading" ) ;
        } catch ( Exception e ){
            System.err.println( "set L&F failed" ) ;
            e.printStackTrace() ;
        }
        frame = new JFrame() ;
        frame.setBounds( UiConsts.MAIN_WINDOW_X,
                UiConsts.MAIN_WINDOW_Y,
                UiConsts.MAIN_WINDOW_WIDTH,
                UiConsts.MAIN_WINDOW_HEIGHT ) ;
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE ) ;
    }

    private void addComponent(){
        JPanel mainPanel = new JPanel( new BorderLayout() ) ;

        mainToolPanel = new JPanel( new BorderLayout() ) ;
        mainPanelCenter = new JPanel( new BorderLayout() ) ;
        mainPanel.add( mainToolPanel , BorderLayout.WEST ) ;
        mainPanel.add( mainPanelCenter , BorderLayout.CENTER ) ;

        toolbarPanel = new ToolbarPanel() ;
        homePanel = new HomePanel() ;
        classesPanel = new ClassesPanel() ;
        forumPanel = new ForumPanel();
        settingsPanel = new SettingsPanel() ;

        mainToolPanel.add( toolbarPanel , BorderLayout.CENTER ) ;
        mainPanelCenter.add( homePanel , BorderLayout.CENTER ) ;

        frame.add( mainPanel ) ;
        mainPanel.updateUI();

    }
}
